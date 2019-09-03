package lib.stat.estimation.provider;

import lib.data.DataContainer;
import lib.data.IntegerData;

public class InsertionEstimationCountProvider extends INDELestimationCountProvider {

	public InsertionEstimationCountProvider(final int maxIterations) {
		super(maxIterations);
	}
	
	@Override
	IntegerData getCount(DataContainer container) {
		return container.getInsertionCount();
	}
}
