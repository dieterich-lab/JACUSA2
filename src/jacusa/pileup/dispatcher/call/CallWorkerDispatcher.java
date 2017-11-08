package jacusa.pileup.dispatcher.call;

import jacusa.cli.parameters.CallParameters;
import jacusa.method.call.CallFactory;
import jacusa.pileup.worker.CallWorker;

import java.io.IOException;

import lib.data.BaseQualData;
import lib.method.AbstractMethodFactory;
import lib.util.coordinateprovider.CoordinateProvider;
import lib.worker.AbstractWorkerDispatcher;

public class CallWorkerDispatcher<T extends BaseQualData> 
extends AbstractWorkerDispatcher<T> {

	public CallWorkerDispatcher(
			final CallFactory callFactory) {
		super(callFactory);
	}

	@Override
	protected CallWorker<T> buildNextWorker() {
		return new CallWorker<T>(this,
				getWorkerContainer().size(),
				getParameters());
	}
	
}
