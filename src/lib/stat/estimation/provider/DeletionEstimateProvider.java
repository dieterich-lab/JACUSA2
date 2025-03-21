package lib.stat.estimation.provider;

import lib.data.DataContainer;

public class DeletionEstimateProvider extends INDELestimateProvider {

	public DeletionEstimateProvider(final int maxIterations) {
		super(maxIterations);
	}
	
	@Override
	int getCount(DataContainer container) {
		return container.getPileupCount().getINDELCount().getDeletionCount();
	}
}
