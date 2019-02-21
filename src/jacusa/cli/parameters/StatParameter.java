package jacusa.cli.parameters;

import lib.stat.AbstractStat;
import lib.stat.AbstractStatFactory;

/**
 * Class that stores necessary statistic parameters such as the factory for the method
 * to calculate the test-statistic and a threshold to filter relevant results. 
 * 
 * @param 
 */
public class StatParameter {

	// chosen statisticCalculator
	private AbstractStatFactory factory;
	private double threshold;
	
	public StatParameter(final AbstractStatFactory factory, final double threshold) {
		this.factory 	= factory;
		this.threshold 	= threshold;
	}

	/**
	 * Default constructor
	 */
	public StatParameter() {
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
	 * Create a new instance of AbstractStatisticCalculator.
	 * 
	 * @return an instance of AbstractStatisticCalculator 
	 */
	public AbstractStat newInstance(final int conditions) {
		return factory.newInstance(threshold, conditions);
	}

	/**
	 * Updates the factory.
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