package lib.stat;

import lib.data.ParallelData;
import lib.data.result.Result;

/**
 * TODO add documentation
 */
public abstract class AbstractStat {

	private int sampleRuns;

	public AbstractStat() {
		this(0);
	}
	
	public AbstractStat(final int sampleRuns) {
		this.sampleRuns = sampleRuns;
	}
	
	protected abstract boolean filter(Result statResult);
	public abstract Result calculate(ParallelData parallelData);
	protected abstract void processAfterCalculate(Result statResult);
	
	public int getSampleRuns() {
		return sampleRuns;
	}
	
	public Result filter(final ParallelData parallelData) {
		final Result statResult = calculate(parallelData);
		if (filter(statResult)) {
			return null;
		}
		processAfterCalculate(statResult);
		return statResult;
	}
	
	
}
