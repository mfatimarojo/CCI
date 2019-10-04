package desmoj.demo.mcburger_model_events;

import desmoj.core.simulator.*;
import java.util.concurrent.TimeUnit;
/**
 * This class represents an entity (and event) source, which continually generates
 * clients (and their arrival events) in order to keep the simulation running.
 *
 * It will create a new client, schedule its arrival at the McBurger (i.e. create
 * and schedule an arrival event) and then schedule itself for the point in
 * time when the next client arrival is due.
 * @author Olaf Neidhardt, Ruth Meyer
 */
public class ClientGeneratorEvent extends ExternalEvent {

	/**
	 * Constructs a new ClientGeneratorEvent.
	 *
	 * @param owner the model this event belongs to
	 * @param name this event's name
	 * @param showInTrace flag to indicate if this event shall produce output for the trace
	 */
	public ClientGeneratorEvent(Model owner, String name, boolean showInTrace) {
		super(owner, name, showInTrace);
	}
	
	/**
	 * The eventRoutine() describes the generating of a new client.
	 *
	 * It creates a new client, a new ClientArrivalEvent
	 * and schedules itself again for the next new client generation.
	 */
	public void eventRoutine() {

		McBurger model = (McBurger)getModel();

		Client client = new Client(model, "Client", true);
		ClientArrivalEvent clientArrival = new ClientArrivalEvent(model, "ClientArrivalEvent", true);
		clientArrival.schedule(client, new TimeSpan(0, TimeUnit.MINUTES));
		schedule(new TimeSpan(model.getClientArrivalTime(), TimeUnit.MINUTES));
	}
}
