package jacusa.method.rtarrest;

import lib.data.ParallelData;
import lib.data.count.PileupCount;
import lib.data.result.OneStatResult;
import lib.data.result.Result;
import lib.stat.AbstractStat;

/**
 * This is a dummy statistic... 
 */
public class CoverageStatistic 
extends AbstractStat {

	@Override
	protected boolean filter(final Result statResult) {
		return false;
	}

	@Override
	public Result calculate(final ParallelData parallelData) {
		final PileupCount pileupCount = parallelData.getCombPooledData().getPileupCount();
		final int coverage = pileupCount.getBCC().getCoverage();
		return new OneStatResult(coverage, parallelData);
	}

	@Override
	protected void addStatResultInfo(final Result statResult) {}
	
}
