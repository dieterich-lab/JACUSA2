package jacusa.worker;

import java.util.Arrays;
import java.util.SortedSet;

import jacusa.method.rtarrest.RTarrestMethod;
import lib.cli.options.filter.has.HasReadSubstitution.BaseSubstitution;
import lib.data.DataContainer;
import lib.data.DataType;
import lib.data.ParallelData;
import lib.data.ParallelData.Builder;
import lib.data.count.BaseSubstitution2BaseCallCount;
import lib.data.fetcher.BaseSubstitution2BaseCallCountAggregator;
import lib.data.fetcher.Fetcher;
import lib.data.result.BaseSubstitutionResult;
import lib.data.result.DeletionCountResult;
import lib.data.result.Result;
import lib.stat.AbstractStat;
import lib.util.ReplicateContainer;
import lib.util.coordinate.Coordinate;
import lib.worker.AbstractWorker;

public class RTArrestWorker
extends AbstractWorker {

	private final AbstractStat stat;
	
	private final Fetcher<BaseSubstitution2BaseCallCount> bs2bccFetcher;
	
	public RTArrestWorker(final RTarrestMethod method, final int threadId) {
		super(method, threadId);
		stat = method.getParameter().getStatParameter()
				.newInstance(method.getParameter().getConditionsSize());
		
		bs2bccFetcher = new BaseSubstitution2BaseCallCountAggregator(
				Arrays.asList(
						DataType.ARREST_BASE_SUBST.getFetcher(), 
						DataType.THROUGH_BASE_SUBST.getFetcher()));
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
		
		final SortedSet<BaseSubstitution> baseSubs = getParameter().getReadSubstitutions();
		if (! baseSubs.isEmpty()) {
			result = new BaseSubstitutionResult(baseSubs, bs2bccFetcher, result);
		}
		
		if (getParameter().showDeletionCount()) {
			result = new DeletionCountResult(baseSubs, result);
		}
		
		return result;
	}
	
}
