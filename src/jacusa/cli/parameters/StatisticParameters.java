package jacusa.cli.parameters;

import jacusa.data.AbstractData;
import jacusa.method.call.statistic.StatisticCalculator;

/**
 * 
 * @author Michael Piechotta
 *
 */
public class StatisticParameters<T extends AbstractData> {

	// filter: statistic
	private StatisticCalculator<T> statisticCalculator;
	private double threshold;
	
	public StatisticParameters() {
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
	public StatisticCalculator<T> getStatisticCalculator() {
		return statisticCalculator;
	}

	/**
	 * @param statisticCalculator the statisticCalculator to set
	 */
	public void setStatisticCalculator(StatisticCalculator<T> statisticCalculator) {
		this.statisticCalculator = statisticCalculator;
	}

}