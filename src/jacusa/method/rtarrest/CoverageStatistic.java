package jacusa.method.rtarrest;

import lib.data.ParallelData;
import lib.data.count.PileupCount;
import lib.data.result.OneStatResult;
import lib.data.result.Result;
import lib.stat.AbstractStat;
import lib.util.ExtendedInfo;

/**
 * This is a dummy statistic... 
 */
class CoverageStatistic extends AbstractStat {
	
	public Result process(final ParallelData parallelData, ExtendedInfo info) {
		final PileupCount pileupCount = parallelData.getCombPooledData().getPileupCount();
		final int coverage = pileupCount.getBCC().getCoverage();
		return new OneStatResult(coverage, parallelData, info);
	}

}
