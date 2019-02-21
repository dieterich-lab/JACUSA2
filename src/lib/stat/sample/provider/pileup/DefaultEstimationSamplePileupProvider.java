package lib.stat.sample.provider.pileup;

import java.util.List;

import lib.data.ParallelData;
import lib.data.count.PileupCount;

public class DefaultEstimationSamplePileupProvider
extends AbstractEstimationSamplePileupProvider {

	public DefaultEstimationSamplePileupProvider(final int maxIterations, final double estimatedError) {

		super(maxIterations, estimatedError);
	}

	@Override
	public List<List<PileupCount>> process(final ParallelData parallelData) {
		return getPileupCounts(parallelData);
	}
	
}
