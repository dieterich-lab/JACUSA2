package jacusa.worker;

import jacusa.method.pileup.PileupMethod;
import lib.data.ParallelData;
import lib.data.result.Result;
import lib.stat.AbstractStat;
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
	protected Result process(final ParallelData parallelData) {
		return stat.filter(parallelData);
	}
		
}
