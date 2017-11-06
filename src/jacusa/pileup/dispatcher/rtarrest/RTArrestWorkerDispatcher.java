package jacusa.pileup.dispatcher.rtarrest;

import jacusa.cli.parameters.RTArrestParameters;
import jacusa.data.BaseQualReadInfoData;

import jacusa.pileup.dispatcher.AbstractWorkerDispatcher;
import jacusa.pileup.worker.RTArrestWorker;
import jacusa.util.coordinateprovider.CoordinateProvider;

import java.io.IOException;

public class RTArrestWorkerDispatcher<T extends BaseQualReadInfoData> 
extends AbstractWorkerDispatcher<T> {

	public RTArrestWorkerDispatcher(
			final CoordinateProvider coordinateProvider,
			final RTArrestParameters<T> parameters) throws IOException {
		super(coordinateProvider, parameters);
	}

	@Override
	protected RTArrestWorker<T> buildNextWorker() {
		return new RTArrestWorker<T>(this, 
				this.getWorkerContainer().size(),
				getParameters());
	}

	public RTArrestParameters<T> getParameters() {
		return (RTArrestParameters<T>) super.getParameters();
	}
	
}
