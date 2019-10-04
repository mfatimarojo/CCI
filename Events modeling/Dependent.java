package desmoj.demo.mcburger_model_events;

import desmoj.core.simulator.*;
/**
 * The dependent entity encapsulates all data relevant for a dependent.
 * In our model, it only stores a reference to the client it is currently
 * (un)attending.
 * @author Olaf Neidhardt, Ruth Meyer
 */
public class Dependent extends Entity {

	/**
	 * Constructor of the van carrier entity.
	 *
	 * @param owner the model this entity belongs to
	 * @param name this dependent's name
	 * @param showInTrace flag to indicate if this entity shall produce output for the trace
	 */
	public Dependent(Model owner, String name, boolean showInTrace) {
		super(owner, name, showInTrace);
	}
}