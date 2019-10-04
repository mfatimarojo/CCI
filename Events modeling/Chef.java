package desmoj.demo.mcburger_model_events;

import desmoj.core.simulator.*;
/**
 * The Chef entity encapsulates all data relevant for a chef.
 * In our model, it only stores a reference to the order it is currently
 * (un)cooking.
 * @author Olaf Neidhardt, Ruth Meyer
 */
public class Chef extends Entity {

	/**
	 * Constructor of the chef entity.
	 *
	 * @param owner the model this entity belongs to
	 * @param name this chef's name
	 * @param showInTrace flag to indicate if this entity shall produce output for the trace
	 */
	public Chef(Model owner, String name, boolean showInTrace) {
		super(owner, name, showInTrace);
	}
}
