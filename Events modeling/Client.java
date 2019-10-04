package desmoj.demo.mcburger_model_events;

import desmoj.core.simulator.*;
/**
 * The Client entity encapsulates all information associated with a client.
 * Due to the fact that the only thing a client wants in our model is a single
 * order, our client has no special attributes.
 * All necessary statistical information are collected by the queue object.
 * @author Olaf Neidhardt, Ruth Meyer
 */
public class Client extends Entity {
	

	private TimeInstant startWait;
	
	private TimeInstant endWait;
	
	/**
	 * Constructor of the client entity.
	 *
	 * @param owner the model this entity belongs to
	 * @param name this client's name
	 * @param showInTrace flag to indicate if this entity shall produce output for the trace
	 */
	public Client(Model owner, String name, boolean showInTrace) {
		super(owner, name, showInTrace);
	}

	public TimeInstant getStartWait() {
		return startWait;
	}

	public void setStartWait(TimeInstant startWait) {
		this.startWait = startWait;
	}

	public TimeInstant getEndWait() {
		return endWait;
	}

	public void setEndWait(TimeInstant endWait) {
		this.endWait = endWait;
	}
	
	public void endWait() {
		this.endWait = presentTime();
	}
	
	public double getWaitTime() {
		if (this.startWait != null && this.endWait != null) 
			return TimeOperations.diff(this.startWait, this.endWait).getTimeAsDouble();
		else
			return Double.NaN;
	}

}
