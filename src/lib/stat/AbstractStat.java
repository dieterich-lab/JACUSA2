package lib.stat;

import lib.data.ParallelData;
import lib.data.result.Result;

/**
 * TODO add documentation
 */
public abstract class AbstractStat {

	protected abstract boolean filter(Result statResult);
	public abstract Result calculate(ParallelData parallelData);

	// TODO check if there are messages add postProcess
	protected abstract void postProcess(Result result, int valueIndex);
	
	public Result process(final ParallelData parallelData) {
		final Result result = calculate(parallelData);
		if (filter(result)) {
			return null;
		}
		postProcess(result, 0);
		return result;
	}

	
	
	
}
