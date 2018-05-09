package jacusa.method.call.statistic;

import lib.data.AbstractData;
import lib.data.ParallelData;
import lib.data.result.StatisticResult;
import lib.util.Info;

/**
 * 
 * @author Michael Piechotta
 */
public abstract class AbstractStatisticCalculator<T extends AbstractData> {

	private final String name;
	private final String desc;
	
	public AbstractStatisticCalculator(final String name, final String desc) {
		this.name = name;
		this.desc = desc;
	}
	
	/**
	 * Add test-statistic to result.
	 * May populate info fields of result.
	 * 
	 * @param result
	 */
	protected abstract void addInfo(final Info info);

	/**
	 * Calculate test-statistic for parallelPileup.
	 * 
	 * @param parallelData
	 * @return
	 */
	public abstract double getStatistic(final ParallelData<T> parallelData);

	public abstract boolean filter(double statistic, double threshold);
	
	public StatisticResult<T> filter(final double threshold, final ParallelData<T> parallelData) {
		final double statistic = getStatistic(parallelData);
		if (filter(statistic, threshold)) {
			return null;
		}
		StatisticResult<T> result = new StatisticResult<T>(statistic, parallelData);
		addInfo(result.getResultInfo());
		return result;
	}

	/**
	 * Returns a new instance of this StatisticCalculator.
	 * @param threshold
	 * @return
	 */
	public abstract AbstractStatisticCalculator<T> newInstance();

	/**
	 * Return the short name of this StatisticCalculator.
	 * @return
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Return a short description of this StatisticCalculator.
	 * @return
	 */
	public String getDescription() {
		return desc;
	}

	/**
	 * Process command lines options.
	 * 
	 * @param line
	 * @return
	 */
	public abstract boolean processCLI(final String line);

}
