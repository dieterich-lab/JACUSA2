package lib.stat;

import lib.data.ParallelData;
import lib.data.result.Result;

/**
 * TODO add documentation
 */
public abstract class AbstractStat {

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
	
	protected abstract boolean filter(Result statResult);
	public abstract Result calculate(ParallelData parallelData);
	protected abstract void processAfterCalculate(Result statResult);
	
	public int getSubsampleRuns() {
		return subsampleRuns;
	}
	
	public int getDownsampleRuns() {
		return downsampleRuns;
	}
	
	public double getDownsampleFraction() {
		return downsampleFraction;
	}
	
	public int getRandomSampleRuns() {
		return randomSampleRuns;
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
