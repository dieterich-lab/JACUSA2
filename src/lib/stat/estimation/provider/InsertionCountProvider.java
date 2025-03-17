package lib.stat.estimation.provider;

import lib.data.DataContainer;
import lib.data.IntegerData;

public class InsertionCountProvider extends INDELestimationCountProvider {

	public InsertionCountProvider(final int maxIterations) {
		super(maxIterations);
	}
	
	@Override
	IntegerData getCount(DataContainer container) {
		return new IntegerData(container.getPileupCount().getINDELCount().getInsertionCount());
	}
}
