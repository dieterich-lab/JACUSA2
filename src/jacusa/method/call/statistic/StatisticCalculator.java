package jacusa.method.call.statistic;

import lib.data.AbstractData;
import lib.data.ParallelData;
import lib.data.Result;

/**
 * 
 * @author Michael Piechotta
 */
public interface StatisticCalculator<T extends AbstractData> {

	/**
	 * Add test-statistic to result.
	 * May populate info fields of result.
	 * 
	 * @param result
	 */
	public void addStatistic(final Result<T> result);
	
	/**
	 * Calculate test-statistic for parallelPileup.
	 * 
	 * @param parallelData
	 * @return
	 */
	public double getStatistic(final ParallelData<T> parallelData);

	/**
	 * Indicates if a value is valid.
	 *   
	 * @param value
	 * @return
	 */
	public boolean filter(final double value);

	/**
	 * Returns a new instance of this StatisticCalculator.
	 * 
	 * @return
	 */
	public StatisticCalculator<T> newInstance();

	/**
	 * Return the short name of this StatisticCalculator.
	 * @return
	 */
	public String getName();
	
	/**
	 * Return a short description of this StatisticCalculator.
	 * @return
	 */
	public String getDescription();

	/**
	 * Process command lines options.
	 * 
	 * @param line
	 * @return
	 */
	public boolean processCLI(final String line);

}
