package jacusa.worker;

import jacusa.method.lrtarrest.LRTarrestMethod;
import lib.data.DataContainer;
import lib.data.ParallelData;
import lib.data.ParallelData.Builder;
import lib.data.result.Result;
import lib.stat.AbstractStat;
import lib.util.ReplicateContainer;
import lib.util.coordinate.Coordinate;
import lib.worker.AbstractWorker;

public class LRTarrestWorker
extends AbstractWorker {

	private final AbstractStat stat;

	public LRTarrestWorker(final LRTarrestMethod method, final int threadId) {
		super(method, threadId);
		stat = method.getParameter().getStatParameter()
				.newInstance(method.getParameter().getConditionsSize());
	}

	@Override
	protected ParallelData createParallelData(Builder parallelDataBuilder, Coordinate coordinate) {
		for (int conditionIndex = 0; conditionIndex < getConditionContainer().getConditionSize() ; ++conditionIndex) {
			final ReplicateContainer replicateContainer = getConditionContainer().getReplicatContainer(conditionIndex);
			for (int replicateIndex = 0; replicateIndex < replicateContainer.getReplicateSize() ; ++replicateIndex) {
				final DataContainer replicate = getConditionContainer().getNullDataContainer(conditionIndex, replicateIndex, coordinate);
				if (replicate == null) {
					return null;
				}
				parallelDataBuilder.withReplicate(conditionIndex, replicateIndex, replicate);
			}	
		}
		return parallelDataBuilder.build();
	}
	
	@Override
	protected Result process(final ParallelData parallelData) {
		return stat.filter(parallelData);
	}
	
}
