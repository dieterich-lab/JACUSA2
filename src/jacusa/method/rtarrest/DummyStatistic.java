package jacusa.method.rtarrest;

import lib.data.ParallelData;
import lib.data.result.OneStatResult;
import lib.data.result.Result;
import lib.stat.AbstractStat;

/**
 * This is a dummy statistic... to display a dummy value 
 */
public class DummyStatistic 
extends AbstractStat {

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
		return new OneStatResult(defaultValue, parallelData);
	}

	@Override
	protected void addStatResultInfo(final Result statResult) {
		// not needed
	}
	
}
