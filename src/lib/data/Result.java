package lib.data;

import lib.util.Info;

/**
 * 
 * @author Michael Piechotta
 *
 */
public class Result<T extends AbstractData> {

	private ParallelData<T> parallelData;
	private double statistic;
	private Info filterInfo;
	private Info resultInfo;
	
	public Result() {
		statistic 	= Double.NaN;
		filterInfo	= new Info();
		resultInfo	= new Info();
	}
	
	/**
	 * 
	 * @param parallelData
	 */
	public void setParallelData(ParallelData<T> parallelData) {
		this.parallelData = parallelData;
	}
	
	/**
	 * 
	 * @return
	 */
	public ParallelData<T> getParellelData() {
		return parallelData;
	}

	/**
	 * 
	 * @param statistic
	 */
	public void setStatistic(double statistic) {
		this.statistic = statistic;
	}
	
	/**
	 * 
	 * @return
	 */
	public double getStatistic() {
		return statistic;
	}
	
	/**
	 * 
	 * @return
	 */
	public Info getResultInfo() {
		return resultInfo;
	}
	
	/**
	 * 
	 * @return
	 */
	public Info getFilterInfo() {
		return filterInfo;
	}

}