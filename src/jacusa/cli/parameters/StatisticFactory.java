package jacusa.cli.parameters;

import jacusa.method.call.statistic.AbstractStatisticCalculator;
import lib.data.AbstractData;

/**
 * 
 * @author Michael Piechotta
 *
 */
public class StatisticFactory<T extends AbstractData> {

	// filter: statistic
	private AbstractStatisticCalculator<T> statisticCalculator;
	private double threshold;
	
	public StatisticFactory(final AbstractStatisticCalculator<T> statisticCalculator, final double threshold) {
		this.statisticCalculator = statisticCalculator;
		this.threshold = threshold;
	}

	public StatisticFactory() {
		threshold = Double.NaN;
	}
	
	/**
	 * @return the maxStat
	 */
	public double getThreshold() {
		return threshold;
	}

	/**
	 * @param threshold the stat to set
	 */
	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}

	/**
	 * @return the statisticCalculator
	 */
	public AbstractStatisticCalculator<T> newInstance() {
		return statisticCalculator.newInstance(threshold);
	}

	/**
	 * @param statisticCalculator the statisticCalculator to set
	 */
	public void setStatisticCalculator(AbstractStatisticCalculator<T> statisticCalculator) {
		this.statisticCalculator = statisticCalculator;
	}

}