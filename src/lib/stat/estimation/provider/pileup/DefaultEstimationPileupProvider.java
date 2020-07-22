package lib.stat.estimation.provider.pileup;

import java.util.List;

import lib.data.ParallelData;
import lib.data.count.PileupCount;

public class DefaultEstimationPileupProvider
extends AbstractEstimationContainerProvider {

	public DefaultEstimationPileupProvider(
			final boolean calcPValue, final int maxIterations, final double estimatedError) {

		super(calcPValue, maxIterations, estimatedError);
	}

	@Override
	public List<List<PileupCount>> process(final ParallelData parallelData) {
		return getPileupCounts(parallelData);
	}

}
