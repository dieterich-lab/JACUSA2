package jacusa.pileup.dispatcher.pileup;

import jacusa.cli.parameters.PileupParameters;
import jacusa.method.pileup.nConditionPileupFactory;
import jacusa.pileup.worker.MpileupWorker;
import lib.data.BaseQualData;
import lib.util.coordinateprovider.CoordinateProvider;
import lib.worker.AbstractWorkerDispatcher;

public class MpileupWorkerDispatcher<T extends BaseQualData> 
extends AbstractWorkerDispatcher<T> {
	
	public MpileupWorkerDispatcher(final nConditionPileupFactory pileupFactory) {
		super(pileupFactory);
	}

	@Override
	protected MpileupWorker<T> buildNextWorker() {
		return new MpileupWorker<T>(this, 
				this.getWorkerContainer().size(), 
				getParameters());
	}
	
}