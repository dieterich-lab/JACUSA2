package lib.stat.estimation.provider;

import lib.data.DataContainer;

public class InsertionEstimateProvider extends INDELestimateProvider {

	public InsertionEstimateProvider(final int maxIterations) {
		super(maxIterations);
	}
	
	@Override
	int getCount(DataContainer container) {
		return container.getPileupCount().getINDELCount().getInsertionCount();
	}
}
