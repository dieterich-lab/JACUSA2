package lib.stat.estimation.provider;

import lib.data.DataContainer;
import lib.data.IntegerData;

public class InsertionEstCountProvider extends INDELestimationCountProvider {

	public InsertionEstCountProvider(final int maxIterations) {
		super(maxIterations);
	}
	
	@Override
	IntegerData getCount(DataContainer container) {
		return new IntegerData(container.getPileupCount().getINDELCount().getInsertionCount());
	}
}
