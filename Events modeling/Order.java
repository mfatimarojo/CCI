package desmoj.demo.mcburger_model_events;

import desmoj.core.simulator.*;
/**
 * The Order entity encapsulates all information associated with an order.
 * All necessary statistical information are collected by the queue object.
 * @author Olaf Neidhardt, Ruth Meyer
 */
public class Order extends Entity {
	
	private Dependent dependent;
	private Client client;

	/**
	 * Constructor of the order entity.
	 *
	 * @param owner the model this entity belongs to
	 * @param name this order's name
	 * @param showInTrace flag to indicate if this entity shall produce output for the trace
	 */
	public Order(Model owner, String name, boolean showInTrace, Dependent dependent, Client client) {
		super(owner, name, showInTrace);
		this.setDependent(dependent);
		this.setClient(client);
	}
	public Dependent getDependent() {
		return dependent;
	}
	public void setDependent(Dependent dependent) {
		this.dependent = dependent;
	}
	public Client getClient() {
		return client;
	}
	public void setClient(Client client) {
		this.client = client;
	}
}
