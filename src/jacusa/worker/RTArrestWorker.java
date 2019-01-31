package jacusa.worker;

import java.util.SortedSet;

import jacusa.method.rtarrest.RTarrestMethod;
import lib.cli.options.filter.has.HasReadSubstitution.BaseSubstitution;
import lib.data.ParallelData;
import lib.data.result.RTarrestBaseSubstitutionResult;
import lib.data.result.Result;
import lib.stat.AbstractStat;
import lib.worker.AbstractWorker;

public class RTArrestWorker
extends AbstractWorker {

	private final AbstractStat stat;
	
	public RTArrestWorker(final RTarrestMethod method, final int threadId) {
		super(method, threadId);
		stat = method.getParameter().getStatParameter()
				.newInstance(method.getParameter().getConditionsSize());
	}

	@Override
	protected Result process(final ParallelData parallelData) {
		final Result result = stat.filter(parallelData); 
		
		final SortedSet<BaseSubstitution> baseSubs = getParameter().getReadSubstitutions();
		if (! getParameter().getReadSubstitutions().isEmpty()) {
			return new RTarrestBaseSubstitutionResult(baseSubs, result);
		}
		
		return result;
	}
	
}
