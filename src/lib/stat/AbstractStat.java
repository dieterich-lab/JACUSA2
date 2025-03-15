package lib.stat;

import lib.data.ParallelData;
import lib.data.result.Result;

/**
 * TODO add documentation
 */
public abstract class AbstractStat {

	// FIXME make a map
	private int subsampleRuns;
	private int downsampleRuns;
	private int randomSampleRuns;
	private double downsampleFraction;

	public AbstractStat() {
		this(0, 0, 0, 0.0);
	}
	
	public AbstractStat(final int subsampleRuns, final int downsampleRuns, final int randomSampleRuns, final double downsampleFraction) {
		this.subsampleRuns = subsampleRuns;
		this.downsampleRuns = downsampleRuns;
		this.randomSampleRuns = randomSampleRuns;
		this.downsampleFraction = downsampleFraction;
	}
	
	// TODO
	protected abstract boolean filter(Result statResult);
	public abstract Result calculate(ParallelData parallelData);
	
	
	@Deprecated
	protected abstract void postProcess(Result statResult);
	
	@Deprecated
	public int getSubsampleRuns() {
		return subsampleRuns;
	}
	
	@Deprecated
	public int getDownsampleRuns() {
		return downsampleRuns;
	}
	
	@Deprecated
	public double getDownsampleFraction() {
		return downsampleFraction;
	}
	
	@Deprecated
	public int getRandomSampleRuns() {
		return randomSampleRuns;
	}
	
	public Result process(final ParallelData parallelData) {
		final Result result = calculate(parallelData);
		if (filter(result)) {
			return null;
		}
		postProcess(result);
		return result;
	}
	
	@Deprecated
	public Result filter(final ParallelData parallelData) {
		final Result statResult = calculate(parallelData);
		if (filter(statResult)) {
			return null;
		}
		postProcess(statResult);
		return statResult;
	}
	
	
}
