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
	protected boolean filter(final Result statResult) {
		return false;
	}

	@Override
	public Result calculate(final ParallelData parallelData) {
		final ExtendedInfo resultInfo = new ExtendedInfo(parallelData.getReplicates());
		return new OneStatResult(defaultValue, parallelData, resultInfo);
	}

	@Override
	protected void postProcess(final Result statResult, final int valueIndex) {
		// not needed
	}
	
}
