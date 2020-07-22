package lib.stat.estimation.provider.arrest;

import java.util.List;

import lib.data.ParallelData;

public class RTarrestEstimationCountProvider extends AbstractRTarrestEstimationCountProvider {


	public RTarrestEstimationCountProvider(final int maxIterations) {
		super(maxIterations, 1d);
	}
	
	@Override
	protected List<List<Count>> process(ParallelData parallelData) {
		return getCounts(parallelData);
	}

}
