package jacusa.worker;

import jacusa.method.rtarrest.RTarrestMethod;
import lib.data.ParallelData;

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
		Result result = stat.process(parallelData); 
		if (result == null) {
			return null;
		}
		
		// FIXME
		processGenericStats(result);
		
		return result;
	}
	
}
