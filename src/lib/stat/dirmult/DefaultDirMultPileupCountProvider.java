package lib.stat.dirmult;

import java.util.List;

import lib.data.ParallelData;
import lib.data.count.PileupCount;

public class DefaultDirMultPileupCountProvider
extends AbstractDirMultPileupCountProvider {

	public DefaultDirMultPileupCountProvider(final int maxIterations, final double estimatedError) {

		super(maxIterations, estimatedError);
	}

	@Override
	public List<List<PileupCount>> process(final ParallelData parallelData) {
		return getPileupCounts(parallelData);
	}
	
}
