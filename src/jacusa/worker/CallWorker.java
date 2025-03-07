package jacusa.worker;

import java.util.TreeSet;

import jacusa.method.call.CallMethod;
import lib.cli.options.filter.has.BaseSub;
import lib.data.DataContainer;
import lib.data.ParallelData;
import lib.data.ParallelData.Builder;
import lib.data.result.DeletionCountResult;
import lib.data.result.InsertionCountResult;
import lib.data.result.Result;
import lib.estimate.MinkaParameter;
import lib.stat.AbstractStat;
import lib.stat.estimation.provider.DeletionEstCountProvider;
import lib.stat.estimation.provider.InsertionEstCountProvider;
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
		for (int condI = 0; condI < getConditionContainer().getConditionSize() ; ++condI) {
			final ReplicateContainer replicateContainer = getConditionContainer().getReplicatContainer(condI);
			for (int replicateI = 0; replicateI < replicateContainer.getReplicateSize() ; ++replicateI) {
				final DataContainer replicate = getConditionContainer().getNullDataContainer(condI, replicateI, coordinate);
				if (replicate == null) {
					return null;
				}
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
		
		if (getParameter().showDeletionCount()) {
			final MinkaParameter minkaPrm = new MinkaParameter();
			final DeletionEstCountProvider delCountProv = 
					new DeletionEstCountProvider(minkaPrm.getMaxIterations());
			result = new DeletionCountResult(new TreeSet<BaseSub>(), result, minkaPrm, delCountProv);
		}
		
		if (getParameter().showInsertionCount() || getParameter().showInsertionStartCount()) {
			final MinkaParameter minkaPrm = new MinkaParameter();
			final InsertionEstCountProvider insCountProv = 
					new InsertionEstCountProvider(minkaPrm.getMaxIterations());
			result = new InsertionCountResult(new TreeSet<BaseSub>(), result, minkaPrm, insCountProv);
		}
		
		return result;
	}
	
}
