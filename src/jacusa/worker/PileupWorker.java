package jacusa.worker;

import jacusa.method.pileup.PileupMethod;
import lib.data.DataContainer;
import lib.data.ParallelData;
import lib.data.ParallelData.Builder;
import lib.data.result.Result;
import lib.stat.AbstractStat;
import lib.util.ReplicateContainer;
import lib.util.coordinate.Coordinate;
import lib.worker.AbstractWorker;

public class PileupWorker
extends AbstractWorker {
	
	private final AbstractStat stat;
	
	public PileupWorker(final PileupMethod method, final int threadId) {
		super(method, threadId);
		stat = method.getParameter().getStatParameter()
				.newInstance(method.getParameter().getConditionsSize());
	}

	@Override
	protected ParallelData createParallelData(Builder parallelDataBuilder, Coordinate coordinate) {
		for (int condI = 0; condI < getConditionContainer().getConditionSize() ; ++condI) {
			final ReplicateContainer replicateContainer = getConditionContainer().getReplicatContainer(condI);
			for (int replicateI = 0; replicateI < replicateContainer.getReplicateSize() ; ++replicateI) {
				final DataContainer replicate = getConditionContainer().getDefaultDataContainer(condI, replicateI, coordinate);
				parallelDataBuilder.withReplicate(condI, replicateI, replicate);
			}	
		}
		return parallelDataBuilder.build();
	}
	
	@Override
	protected Result process(final ParallelData parallelData) {
		Result result = stat.filter(parallelData); 
		if (result == null) {
			return null;
		}
		
		// TODO process INDELs
		
		return result;
	}
	
}
