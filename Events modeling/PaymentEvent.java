package desmoj.demo.mcburger_model_events;

import desmoj.core.simulator.*;
import java.util.concurrent.TimeUnit;

/**
 * This class represents the payment event
 * in the McBurger model.
 * It occurs when a order is payed at McBurger
 * by its client.
 * @author Olaf Neidhardt, Ruth Meyer
 */
public class PaymentEvent extends EventOf2Entities<Dependent,Client> {
	

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
	public PaymentEvent(Model owner, String name, boolean showInTrace) {
		super(owner, name, showInTrace);
		myModel = (McBurger)owner;
	}

	/**
	 * This eventRoutine() describes what happens when an order
	 * is payed at McBurger.
	 *
	 * The client will leave McBurger.
	 *
	 * It will be check if there are more clients waiting.
	 * If this is the case, it will occupy the dependent and schedule an
	 * order arrival event.
	 * Otherwise the dependent is added to the idle queue.
	 */
	@Override
	public void eventRoutine(Dependent dependent, Client client) {

		sendTraceNote(client + " pays to " + dependent + " and leaves McBurger.");
        client.endWait();
		myModel.waitTimeHistogram.update(client.getWaitTime());

		// check if there are other clients waiting
        sendTraceNote(dependent + " check if there are more clients waiting at McBurger.");
		if (!myModel.clientQueue.isEmpty())
		{
			sendTraceNote("There are more clients.");
			// remove the first waiting client from the queue
			Client nextClient = myModel.clientQueue.first();
			myModel.clientQueue.remove(nextClient);
			sendTraceNote(nextClient + " is being attended by " + dependent + ".");
			sendTraceNote("Clients queue: " + myModel.clientQueue.length() + ". Available dependents: " + myModel.idleDependentQueue.length() + ".");

			Order order = new Order(myModel, "Order", true, dependent, nextClient);
			OrderArrivalEvent orderArrival = new OrderArrivalEvent(myModel, "OrderArrivalEvent", true);
			orderArrival.schedule(order, new TimeSpan(myModel.getServiceTimeClientDependent(), TimeUnit.MINUTES));
		}
		else {
			sendTraceNote("Still no clients.");
			// --> the dependent is placed on its McBurger cash register
			myModel.idleDependentQueue.insert(dependent);
			sendTraceNote("Available dependents: " + myModel.idleDependentQueue.length() + ".");
		}
	}

}