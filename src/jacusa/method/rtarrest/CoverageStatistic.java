package jacusa.method.rtarrest;

import lib.data.DataType;
import lib.data.ParallelData;
import lib.data.count.basecall.BaseCallCount;
import lib.data.result.OneStatResult;
import lib.data.result.Result;
import lib.stat.AbstractStat;

/**
 * This is a dummy statistic...
 */
class CoverageStatistic extends AbstractStat {

	private final DataType<BaseCallCount> dataType;

	public CoverageStatistic() {
		this.dataType = DataType.get("default", BaseCallCount.class);
	}

	@Override
	protected boolean filter(final Result statResult) {
		return false;
	}

	@Override
	public Result calculate(final ParallelData parallelData) {
		final int coverage = parallelData.getCombPooledData().get(dataType).getCoverage();
		return new OneStatResult(coverage, parallelData);
	}

	@Override
	protected void addStatResultInfo(final Result statResult) {
	}

}
