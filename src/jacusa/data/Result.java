package jacusa.data;

import jacusa.util.Info;

/**
 * 
 * @author Michael Piechotta
 *
 */
public class Result<T extends AbstractData> {

	private ParallelPileupData<T> parallelData;
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
	public void setParallelData(ParallelPileupData<T> parallelData) {
		this.parallelData = parallelData;
	}
	
	/**
	 * 
	 * @return
	 */
	public ParallelPileupData<T> getParellelData() {
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