package lib.stat;

import lib.data.ParallelData;
import lib.data.result.Result;

/**
 * 
 * 
 */
public abstract class AbstractStat {

	protected abstract boolean filter(Result statResult);
	public abstract Result calculate(ParallelData parallelData);
	protected abstract void addStatResultInfo(Result statResult);
	
	public Result filter(final ParallelData parallelData) {
		final Result statResult = calculate(parallelData);
		if (filter(statResult)) {
			return null;
		}
		addStatResultInfo(statResult);
		return statResult;
	}
	
	
}
