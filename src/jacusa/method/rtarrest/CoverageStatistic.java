package jacusa.method.rtarrest;

import lib.data.ParallelData;
import lib.data.count.PileupCount;
import lib.data.result.OneStatResult;
import lib.data.result.Result;
import lib.stat.AbstractStat;

/**
 * This is a dummy statistic... TODO add comments
 *  
 * @param 
 */
public class CoverageStatistic 
extends AbstractStat {

	public CoverageStatistic(final CoverageStatisticFactory factory) {
		super(factory);
	}

	@Override
	protected boolean filter(final Result statResult) {
		return false;
	}

	@Override
	public Result calculate(final ParallelData parallelData) {
		final PileupCount pileupCount = parallelData.getCombinedPooledData().getPileupCount();
		final int coverage = pileupCount.getBaseCallCount().getCoverage();
		return new OneStatResult(coverage, parallelData);
	}

	@Override
	protected void addStatResultInfo(final Result statResult) {}
	
}