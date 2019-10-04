package desmoj.demo.mcburger_model_events;

import java.util.concurrent.TimeUnit;

import desmoj.core.simulator.*;
/**
 * This class represents the order arrival event
 * in the McBurger model.
 * It occurs when a order is placed at McBurger
 * as a request of a client.
 * @author Olaf Neidhardt, Ruth Meyer
 */
public class OrderArrivalEvent extends Event<Order> {

	/** a reference to the model this event is a part of.
	 * Useful shortcut to access the model's static components
	 */
	private McBurger myModel;

	/**
	 * Constructor of the order arrival event
	 *
	 * Used to create a new order arrival event
	 *
	 * @param owner the model this event belongs to
	 * @param name this event's name
	 * @param showInTrace flag to indicate if this event shall produce output for the trace
	 */
	public OrderArrivalEvent(Model owner, String name, boolean showInTrace) {
		super(owner, name, showInTrace);
		myModel = (McBurger)owner;
	}

	/**
	 * This eventRoutine() describes what happens when an order
	 * is placed at McBurger.
	 *
	 * On arrival, the order will enter the queue (order's queue waiting to be cooked). It will then
	 * check if the chef is available.
	 * If this is the case, it will occupy the chef and schedule an
	 * event to prepare the order.
	 * Otherwise the order just waits (does nothing).
	 */
	public void eventRoutine(Order order) {
		
		// order is placed at McBurger
		myModel.orderQueue.insert(order);
		sendTraceNote(order.getClient() + " places " + order + ".");
		sendTraceNote("Pending orders: " + myModel.orderQueue.length() + ".");
        
		// check if a chef is available
		if (!myModel.idleChefQueue.isEmpty()) {

			// get a reference to the first chef from the idle chef queue
			Chef chef = myModel.idleChefQueue.first();
			// remove it from the queue
			myModel.idleChefQueue.remove(chef);
			// remove the order from the queue
			myModel.orderQueue.remove(order);
			sendTraceNote(order + " is being prepared by " + chef + ".");
			sendTraceNote("Pending orders: " + myModel.orderQueue.length() + ". Available chefs: " + myModel.idleChefQueue.length() + ".");
			
			// create a new order departure event
			OrderDepartureEvent orderDeparture = new OrderDepartureEvent(myModel, "OrderDepartureEvent", true);
			// and schedule it
			orderDeparture.schedule(chef, order, new TimeSpan(myModel.getServiceTimeOrderChef(), TimeUnit.MINUTES));
		}
	}
}