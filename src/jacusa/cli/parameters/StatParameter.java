package jacusa.cli.parameters;

import lib.stat.AbstractStat;
import lib.stat.AbstractStatFactory;

/**
 * Class that stores necessary statistic parameters such as the factory for the method
 * to calculate the test-statistic and a threshold to filter relevant results. 
 */
public class StatParameter {

	// chosen statisticCalculator
	private AbstractStatFactory factory;
	private double threshold;
	
	public StatParameter(
			final AbstractStatFactory factory, final double threshold) {
		
		this.factory 	= factory;
		this.threshold 	= threshold;
	}

	/**
	 * Default constructor
	 */
	public StatParameter() {
		// Double.Nan implies no filtering
		threshold = Double.NaN;
	}

	/**
	 * Returns an double that is used to filter the test-statistic.
	 * 
	 * @return threshold
	 */
	public double getThreshold() {
		return threshold;
	}

	/**
	 * Updates the threshold.
	 * 
	 * @param threshold new threshold to be set 
	 */
	public void setThreshold(final double threshold) {
		this.threshold = threshold;
	}

	/**
	 * Create a new instance of AbstractStatisticCalculator for specific number 
	 * of conditions.
	 * 
	 * @param conditions the number of conditions to be used
	 * @return an instance of AbstractStatisticCalculator 
	 */
	public AbstractStat newInstance(final int conditions) {
		return factory.newInstance(threshold, conditions);
	}

	/**
	 * Updates the factory - set any CLI options.
	 * 
	 * @param new factory to be set 
	 */
	public void setFactory(
			final String CLIoption, 
			final AbstractStatFactory factory) {
		
		factory.processCLI(CLIoption);
		this.factory = factory;
	}

	public AbstractStatFactory getFactory() {
		return factory;
	}
	
}
