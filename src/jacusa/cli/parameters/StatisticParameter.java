package jacusa.cli.parameters;

import jacusa.method.call.statistic.AbstractStatisticCalculator;
import lib.data.AbstractData;

/**
 * Class that stores necessary statistic parameters such as the method
 * to calculate the test-statistic and a threshold to filter relevant results. 
 * 
 * @param <T>
 */
public class StatisticParameter<T extends AbstractData> {

	// chosen statisticCalculator
	private AbstractStatisticCalculator<T> statisticCalculator;
	private double threshold;
	private String CLIoption;
	
	public StatisticParameter(final AbstractStatisticCalculator<T> statisticCalculator, 
			final double threshold) {

		this.statisticCalculator 	= statisticCalculator;
		this.threshold 				= threshold;
		CLIoption 					= new String();
	}

	/**
	 * Default constructor
	 */
	public StatisticParameter() {
		threshold = Double.NaN;
		CLIoption = new String();
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
	public AbstractStatisticCalculator<T> newInstance() {
		AbstractStatisticCalculator<T> o = statisticCalculator.newInstance();
		o.processCLI(CLIoption);
		return o;
	}

	/**
	 * Updates the statisticCalculator.
	 * 
	 * @param statisticCalculator new statisticCalculator to be set 
	 */
	public void setStatisticCalculator(final String CLIoption, 
			final AbstractStatisticCalculator<T> statisticCalculator) {

		this.CLIoption = CLIoption;
		this.statisticCalculator = statisticCalculator;
	}

	public String getStatisticCalculatorName() {
		return statisticCalculator.getName();
	}

}