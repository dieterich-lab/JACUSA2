package jacusa.pileup.dispatcher.call;

import jacusa.cli.parameters.CallParameters;
import jacusa.data.BaseQualData;
import jacusa.pileup.dispatcher.AbstractWorkerDispatcher;
import jacusa.pileup.worker.CallWorker;
import jacusa.util.coordinateprovider.CoordinateProvider;

import java.io.IOException;

public class CallWorkerDispatcher<T extends BaseQualData> 
extends AbstractWorkerDispatcher<T> {

	public CallWorkerDispatcher(
			final CoordinateProvider coordinateProvider,
			final CallParameters<T> parameters) throws IOException {
		super(coordinateProvider, parameters);
	}

	@Override
	protected CallWorker<T> buildNextWorker() {
		return new CallWorker<T>(this,
				getWorkerContainer().size(),
				getParameters());
	}

	@Override
	public CallParameters<T> getParameters() {
		return (CallParameters<T>)super.getParameters();
	}
	
}
