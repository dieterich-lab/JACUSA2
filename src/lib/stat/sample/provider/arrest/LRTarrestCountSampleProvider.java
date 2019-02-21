package lib.stat.sample.provider.arrest;

import lib.data.ParallelData;
import lib.stat.sample.EstimationSample;
import lib.stat.sample.provider.EstimationSampleProvider;

public class LRTarrestCountSampleProvider implements EstimationSampleProvider {

	public static final int READ_TOTAL_INDEX	= 0;
	public static final int READ_ARREST_INDEX	= 1;

	// private final int maxIterations;
	// private final double pseudoCount;

	public LRTarrestCountSampleProvider(final int maxIterations) {
		// this.maxIterations 	= maxIterations;
		// this.pseudoCount 	= 1d;
	}
	
	@Override
	public EstimationSample[] convert(ParallelData parallelData) {
		final int conditions = parallelData.getConditions();
		final EstimationSample[] estimationSamples = new EstimationSample[conditions + 1];
		// TODO
		return estimationSamples;
	}
	
}
