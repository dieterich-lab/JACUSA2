package jacusa.method.rtarrest;

import lib.data.ParallelData;
import lib.data.result.OneStatResult;
import lib.data.result.Result;
import lib.stat.AbstractStat;
import lib.util.ExtendedInfo;

/**
 * This is a dummy statistic... to display a dummy value 
 */
class DummyStatistic extends AbstractStat {

	private final double defaultValue;

	public DummyStatistic() {
		defaultValue = Double.NaN;
	}

	@Override
	public Result process(final ParallelData parallelData, ExtendedInfo info) {
		final ExtendedInfo resultInfo = new ExtendedInfo();
		return new OneStatResult(defaultValue, parallelData, resultInfo);
	}
	
}
