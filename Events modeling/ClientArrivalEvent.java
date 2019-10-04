package desmoj.demo.mcburger_model_events;

import java.util.concurrent.TimeUnit;

import desmoj.core.simulator.*;

/**
 * This class represents the client arrival event
 * in the McBurger model.
 * It occurs when a client arrives at the McBurger
 * to request an order.
 * @author Olaf Neidhardt, Ruth Meyer
 */
public class ClientArrivalEvent extends Event<Client> {

	/** a reference to the model this event is a part of.
	 * Useful shortcut to access the model's static components
	 */
	private McBurger myModel;

	/**
	 * Constructor of the client arrival event
	 *
	 * Used to create a new client arrival event
	 *
	 * @param owner the model this event belongs to
	 * @param name this event's name
	 * @param showInTrace flag to indicate if this event shall produce output for the trace
	 */
	public ClientArrivalEvent(Model owner, String name, boolean showInTrace) {
		super(owner, name, showInTrace);
		myModel = (McBurger)owner;
	}

	/**
	 * This eventRoutine() describes what happens when a client
	 * arrives at McBurger.
	 *
	 * On arrival, the client will enter the queue (client's queue waiting at McBurger). It will then
	 * check if the dependent is available.
	 * If this is the case, it will occupy the dependent and schedule a
	 * service end event.
	 * Otherwise the client just waits (does nothing).
	 */
	public void eventRoutine(Client client) {

		// client enters McBurger
		myModel.clientQueue.insert(client);
		client.setStartWait(presentTime());
		sendTraceNote(client + " arrives at McBurger.");
		sendTraceNote("Clients queue: " + myModel.clientQueue.length() + ".");

		// check if a dependent is available
		if (!myModel.idleDependentQueue.isEmpty()) {

			// get a reference to the first dependent from the idle dependent queue
			Dependent dependent = myModel.idleDependentQueue.first();
			// remove it from the queue
			myModel.idleDependentQueue.remove(dependent);
			// remove the client from the queue
			myModel.clientQueue.remove(client);
			sendTraceNote(client + " is being attended by " + dependent + ".");
			sendTraceNote("Clients queue: " + myModel.clientQueue.length() + ". Available dependents: " + myModel.idleDependentQueue.length() + ".");
 
			Order order = new Order(myModel, "Order", true, dependent, client);
			// create a new order arrival event
			OrderArrivalEvent orderArrival = new OrderArrivalEvent(myModel, "OrderArrivalEvent", true);
			// and schedule it
			orderArrival.schedule(order, new TimeSpan(myModel.getServiceTimeClientDependent(), TimeUnit.MINUTES));
		}
	}
}