package jacusa.worker;

import jacusa.method.lrtarrest.LRTarrestMethod;
import lib.data.ParallelData;
import lib.data.result.Result;
import lib.stat.AbstractStat;
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
	protected Result process(final ParallelData parallelData) {
		return stat.filter(parallelData);
	}
	
}
