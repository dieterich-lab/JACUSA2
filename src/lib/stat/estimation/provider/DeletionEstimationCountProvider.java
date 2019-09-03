package lib.stat.estimation.provider;

import lib.data.DataContainer;
import lib.data.IntegerData;

public class DeletionEstimationCountProvider extends INDELestimationCountProvider {

	public DeletionEstimationCountProvider(final int maxIterations) {
		super(maxIterations);
	}
	
	@Override
	IntegerData getCount(DataContainer container) {
		return container.getDeletionCount();
	}
}
