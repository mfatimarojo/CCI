package desmoj.demo.mcburger_model_events;

import desmoj.core.simulator.*;
import desmoj.core.statistic.Histogram;
import desmoj.core.dist.*;
import java.util.concurrent.TimeUnit;

/**
 * This is the model class. It is the main class of a simple event-oriented
 * model of clients placing an order at McBurgers. Clients arrive at McBurger
 * to ask for an order. They wait in line until a dependent is available to 
 * attend the client. When the order is placed, the order waits until a chef is
 * available to cook while the client waits at McBurgers for his order to be ready.
 * When his order is ready, the client leaves McBurger while the dependent
 * serves the next client.
 * 
 * @author Olaf Neidhardt, Ruth Meyer
 */
public class McBurger extends Model {

	/**
	 * model parameter: the number of dependents
	 */
	protected static int NUM_DEPENDENTS = 4;
	
	/**
	 * model parameter: the number of chefs
	 */
	protected static int NUM_CHEFS = 1;
	

	/**
	 *  To determine the next client arrival time.
	 */
	private ContDistExponential clientArrivalTime;

	/**
	 * To determine the service time related to placing an order.
	 */
	private ContDistExponential serviceTimeClientDependent;
	
	/**
	 * To determine the service time related to preparing an order.
	 */
	private ContDistExponential serviceTimeOrderChef;
	
	/**
	 * To determine the service time related to payment.
	 */
	private ContDistExponential serviceTimePayment;

	/**
	 * A waiting queue object is used to represent client's queue at McBurger.
	 * Every time a client arrives it is inserted into this queue and will be 
	 * removed when attended by a dependent.
	 */
	protected Queue<Client> clientQueue;
	
	/**
	 * A waiting queue object is used to represent order's queue at McBurger.
	 * Every time an order is placed it is inserted into this queue and will be 
	 * removed when attended by a chef.
	 */
	protected Queue<Order> orderQueue;

	/**
	 * A waiting queue object is used to represent the McBurger cash register for the dependent.
	 * If there is no client waiting for service the dependent will wait for the next client to come.
	 */
	protected Queue<Dependent> idleDependentQueue;
	
	/**
	 * To represent the McBurger kitchen stand for the chef. If there is no order waiting to be 
	 * prepared, the chef wait for the next order to come.
	 */
	protected Queue<Chef> idleChefQueue;
	
	protected Histogram waitTimeHistogram;

	/**
	 * McBurger constructor.
	 *
	 * Creates a new McBurger model via calling the constructor of the
	 * superclass.
	 *
	 * @param owner
	 *            the model this model is part of (set to <tt>null</tt> when
	 *            there is no such model)
	 * @param modelName
	 *            this model's name
	 * @param showInReport
	 *            flag to indicate if this model shall produce output to the
	 *            report file
	 * @param showInTrace
	 *            flag to indicate if this model shall produce output to the
	 *            trace file
	 */
	public McBurger(Model owner, String modelName, boolean showInReport, boolean showInTrace) {
		super(owner, modelName, showInReport, showInTrace);
	}

	/**
	 * Returns a description of the model to be used in the report.
	 * 
	 * @return model description as a string
	 */
	public String description() {
		return "This model describes a queueing system at Mcburger. Clients arrive and "
				+ "place an order. A dependent attends the client and adds his order "
				+ "to the order's list. The chef cooks the list of orders placed while "
				+ "the client waits until his order is ready. Afterwards, the client "
				+ "leaves McBurger. In case the dependent is busy, the client waits "
				+ "for its turn at McBurgers. If the dependent is idle, it waits on "
				+ "its own McBurger cash register for the client to come. In case the "
				+ "chef is busy, the client waits for its order at McBurgers. If the chef "
				+ "is idle, it waits on its own McBurger kitchen stand for orders to come.";
	}

	/**
	 * Activates dynamic model components (events).
	 *
	 * This method is used to place all events or processes on the internal
	 * event list of the simulator which are necessary to start the simulation.
	 *
	 * In this case, the client generator event will have to be created and
	 * scheduled for the start time of the simulation.
	 */
	public void doInitialSchedules() {

		ClientGeneratorEvent clientGenerator = new ClientGeneratorEvent(this, "ClientGenerator", true);
		clientGenerator.schedule(new TimeSpan(0, TimeUnit.MINUTES));	
	}
	
	/**
	 * Initialises static model components like distributions and queues.
	 */
	public void init() {
		
		waitTimeHistogram = new Histogram(this, "Client Wait Times", 0, 16, 10, true, false);

		clientArrivalTime = new ContDistExponential(this, "ClientArrivalTimeStream", 7.0, true, false);
		clientArrivalTime.setNonNegative(true);
		serviceTimeClientDependent = new ContDistExponential(this, "ServiceTimeClientDependentStream", 5.0, true, false);
		serviceTimeClientDependent.setNonNegative(true);
		serviceTimeOrderChef = new ContDistExponential(this, "ServiceTimeOrderChefStream", 10.0, true, false);
		serviceTimeOrderChef.setNonNegative(true);
		serviceTimePayment = new ContDistExponential(this, "PaymentArrivalTimeStream", 2.0, true, false);
		serviceTimePayment.setNonNegative(true);
		
		clientQueue = new Queue<Client>(this, "Client Queue", true, true);
		idleDependentQueue = new Queue<Dependent>(this, "idle dependent Queue", true, true);
		orderQueue = new Queue<Order>(this, "Order Queue", true, true);
		idleChefQueue = new Queue<Chef>(this, "idle chef Queue", true, true);
		
		Dependent dependent;
		for (int i = 0; i < NUM_DEPENDENTS; i++) {
			dependent = new Dependent(this, "Dependent", true);
			idleDependentQueue.insert(dependent);
		}
		
		Chef chef;
		for (int i = 0; i < NUM_CHEFS; i++) {
			chef = new Chef(this, "Chef", true);
			idleChefQueue.insert(chef);
		}			
	}
	
	/**
	 * Returns a sample of the random stream used to determine the next client
	 * arrival time.
	 *
	 * @return double a clientArrivalTime sample
	 */
	public double getClientArrivalTime() {
		return clientArrivalTime.sample();
	}

	/**
	 * Returns a sample of the random stream used to determine the service time
	 * related to placing an order.
	 *
	 * @return double a serviceTime sample
	 */
	public double getServiceTimeClientDependent() {
		return serviceTimeClientDependent.sample();
	}
	
	/**
	 * Returns a sample of the random stream used to determine the service time
	 * related to preparing an order.
	 *
	 * @return double a serviceTimeOrderChef sample
	 */
	public double getServiceTimeOrderChef() {
		return serviceTimeOrderChef.sample();
	}
	
	/**
	 * Returns a sample of the random stream used to determine the service time
	 * related to the payment.
	 *
	 * @return double a serviceTimePayment sample
	 */
	public double getServiceTimePayment() {
		return serviceTimePayment.sample();
	}

	/**
	 * Runs the model.
	 *
	 * In DESMO-J used to - instantiate the experiment - instantiate the model -
	 * connect the model to the experiment - steer length of simulation and
	 * outputs - set the ending criterion (normally the time) - start the
	 * simulation - initiate reporting - clean up the experiment
	 *
	 * @param args
	 *            is an array of command-line arguments (will be ignored here)
	 */
	public static void main(java.lang.String[] args) {

		McBurger model = new McBurger(null, "McBurger", true, true);
		
		Experiment.setEpsilon(TimeUnit.SECONDS);
		Experiment.setReferenceUnit(TimeUnit.MINUTES);
		Experiment exp = new Experiment("McBurgerExperiment");		

		model.connectToExperiment(exp);

		exp.setShowProgressBar(true); 
		exp.stop(new TimeInstant(1500, TimeUnit.MINUTES)); 
		exp.tracePeriod(new TimeInstant(0), new TimeInstant(100, TimeUnit.MINUTES)); 																				// trace
		exp.debugPeriod(new TimeInstant(0), new TimeInstant(50, TimeUnit.MINUTES)); 
		exp.start();
		exp.report();
		exp.finish();
	}
}