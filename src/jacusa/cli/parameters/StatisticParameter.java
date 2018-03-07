package jacusa.cli.parameters;

import jacusa.method.call.statistic.AbstractStatisticCalculator;
import lib.data.AbstractData;

/**
 * @author Michael Piechotta
 */
public class StatisticParameter<T extends AbstractData> {

	// chosen statisticCalculator
	private AbstractStatisticCalculator<T> statisticCalculator;
	private double threshold;
	
	public StatisticParameter(final AbstractStatisticCalculator<T> statisticCalculator, 
			final double threshold) {

		this.statisticCalculator 	= statisticCalculator;
		this.threshold 				= threshold;
	}

	/**
	 * Default constructor
	 */
	public StatisticParameter() {
		threshold = Double.NaN;
	}
	
	/**
	 * Returns an double that is used to filter the test-statistic.
	 * @return threshold
	 */
	public double getThreshold() {
		return threshold;
	}

	/**
	 * Updates the threshold.
	 * @param threshold new threshold to be set 
	 */
	public void setThreshold(final double threshold) {
		this.threshold = threshold;
	}

	/**
	 * Create a new instance of AbstractStatisticCalculator.
	 * @return an instance of AbstractStatisticCalculator 
	 */
	public AbstractStatisticCalculator<T> newInstance() {
		return statisticCalculator.newInstance(threshold);
	}

	/**
	 * Updates the statisticCalculator.
	 * @param statisticCalculator new statisticCalculator to be set 
	 */
	public void setStatisticCalculator(final AbstractStatisticCalculator<T> statisticCalculator) {
		this.statisticCalculator = statisticCalculator;
	}

}