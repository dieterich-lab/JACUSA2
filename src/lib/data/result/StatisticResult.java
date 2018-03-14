package lib.data.result;


import lib.data.AbstractData;
import lib.data.ParallelData;
import lib.util.Info;

public class StatisticResult<T extends AbstractData> 
implements Result<T>, hasStatistic {

	private final double statistic;
	private final Result<T> result;

	public StatisticResult(final double statistic, final ParallelData<T> parallelData) {
		this.statistic = statistic;
		this.result = new DefaultResult<T>(parallelData);
	}

	@Override
	public double getStatistic() {
		return statistic;
	}

	@Override
	public ParallelData<T> getParellelData() {
		return result.getParellelData();
	}

	@Override
	public Info getResultInfo() {
		return result.getResultInfo();
	}

	@Override
	public Info getFilterInfo() {
		return result.getFilterInfo();
	}

	@Override
	public void setFiltered(boolean marked) {
		result.setFiltered(marked);
	}

	@Override
	public boolean isFiltered() {
		return result.isFiltered();
	}
	
}
