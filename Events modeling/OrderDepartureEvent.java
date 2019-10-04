package desmoj.demo.mcburger_model_events;

import desmoj.core.simulator.*;
import java.util.concurrent.TimeUnit;
/**
 * This class represents the order departure event
 * in the McBurger model.
 * It occurs when an order is ready at McBurger.
 * @author Olaf Neidhardt, Ruth Meyer
 */
public class OrderDepartureEvent extends EventOf2Entities<Chef, Order> {
	

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
	public OrderDepartureEvent(Model owner, String name, boolean showInTrace) {
		super(owner, name, showInTrace);
		myModel = (McBurger)owner;
	}

	/**
	 * This eventRoutine() describes what happens when an order
	 * is ready at McBurger.
	 *
	 * The client will pay his/her order by scheduling a
	 * payment event.
	 *
	 * It will be check if there are more orders waiting.
	 * If this is the case, it will occupy the chef and schedule a
	 * order departure event.
	 * Otherwise the chef is added to the idle queue.
	 */
	public void eventRoutine(Chef chef, Order order) {
		
		// order is ready at McBurger
        sendTraceNote(order.getClient() + " 's " + order + " is ready! " + order.getDependent() + " serves " + order + " to " + order.getClient() + " and waits for the payment.");
        
		// create a new payment event
		PaymentEvent paymentArrival = new PaymentEvent(myModel, "PaymentArrivalEvent", true);
		// and schedule it
		paymentArrival.schedule(order.getDependent(), order.getClient(), new TimeSpan(myModel.getServiceTimePayment(),TimeUnit.MINUTES));
 
		// check if there are other orders waiting
		sendTraceNote(chef + " check if there are more orders waiting to be prepared.");

		if (!myModel.orderQueue.isEmpty())
		{
			// remove the first waiting order from the queue
			sendTraceNote("There are more orders.");
			Order nextOrder = myModel.orderQueue.first();
			myModel.orderQueue.remove(nextOrder);
			sendTraceNote(nextOrder + " is being prepared by " + chef + ".");
			sendTraceNote("Pending orders: " + myModel.orderQueue.length() + ". Available chefs: " + myModel.idleChefQueue.length() + ".");

			// create a new order departure event
			OrderDepartureEvent event = new OrderDepartureEvent(myModel, "ServiceEndEvent", true);
 			// and schedule it
			event.schedule(chef, nextOrder, new TimeSpan(myModel.getServiceTimeOrderChef(), TimeUnit.MINUTES));
		}
		else {
			sendTraceNote("Still no orders.");
			// --> the chef is placed on its idle queue
			myModel.idleChefQueue.insert(chef);
			sendTraceNote("Available chefs: " + myModel.idleChefQueue.length() + ".");
		}	
	}
}