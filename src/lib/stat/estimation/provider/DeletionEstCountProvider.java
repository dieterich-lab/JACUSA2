package lib.stat.estimation.provider;

import lib.data.DataContainer;
import lib.data.IntegerData;

public class DeletionEstCountProvider extends INDELestimationCountProvider {

	public DeletionEstCountProvider(final int maxIterations) {
		super(maxIterations);
	}
	
	@Override
	IntegerData getCount(DataContainer container) {
		return new IntegerData(container.getPileupCount().getINDELCount().getDeletionCount());
	}
}
