package jacusa.worker;

import java.util.SortedSet;

import jacusa.method.call.CallMethod;
import lib.cli.options.filter.has.BaseSub;
import lib.data.DataContainer;
import lib.data.DataType;
import lib.data.ParallelData;
import lib.data.ParallelData.Builder;
import lib.data.result.BaseSubstitutionResult;
import lib.data.result.DeletionCountResult;
import lib.data.result.InsertionCountResult;
import lib.data.result.Result;
import lib.estimate.MinkaParameter;
import lib.stat.AbstractStat;
import lib.stat.estimation.provider.DeletionEstimationCountProvider;
import lib.stat.estimation.provider.InsertionEstimationCountProvider;
import lib.util.ReplicateContainer;
import lib.util.coordinate.Coordinate;
import lib.worker.AbstractWorker;

/**
 * Method "call" specific worker.
 */
public class CallWorker extends AbstractWorker {

	private final AbstractStat stat;

	public CallWorker(final CallMethod method, final int threadId) {
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
		Result result = stat.filter(parallelData); 
		
		final SortedSet<BaseSub> baseSubs = getParameter().getReadSubstitutions();
		if (! baseSubs.isEmpty()) {
			result = new BaseSubstitutionResult(baseSubs, DataType.BASE_SUBST2BCC.getFetcher(), result);
		}
		
		if (getParameter().showDeletionCount()) {
			final MinkaParameter minkaPrm = new MinkaParameter();
			final DeletionEstimationCountProvider delCountProv = 
					new DeletionEstimationCountProvider(minkaPrm.getMaxIterations());
			result = new DeletionCountResult(baseSubs, result, minkaPrm, delCountProv);
		}
		
		if (getParameter().showInsertionCount()) {
			final MinkaParameter minkaPrm = new MinkaParameter();
			final InsertionEstimationCountProvider insCountProv = 
					new InsertionEstimationCountProvider(minkaPrm.getMaxIterations());
			result = new InsertionCountResult(baseSubs, result, minkaPrm, insCountProv);
		}
		
		return result;
	}
	
}
