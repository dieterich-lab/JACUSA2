package jacusa.pileup.dispatcher.pileup;

import jacusa.cli.parameters.PileupParameters;
import jacusa.data.BaseQualData;

import jacusa.pileup.dispatcher.AbstractWorkerDispatcher;
import jacusa.pileup.worker.MpileupWorker;
import jacusa.util.coordinateprovider.CoordinateProvider;

public class MpileupWorkerDispatcher<T extends BaseQualData> 
extends AbstractWorkerDispatcher<T> {
	
	public MpileupWorkerDispatcher(
			final CoordinateProvider coordinateProvider, 
			final PileupParameters<T> parameters) {
		super(coordinateProvider, parameters);
	}

	@Override
	protected MpileupWorker<T> buildNextWorker() {
		return new MpileupWorker<T>(this, 
				this.getWorkerContainer().size(), 
				getParameters());
	}

	@Override
	public PileupParameters<T> getParameters() {
		return (PileupParameters<T>) super.getParameters();
	}
	
}