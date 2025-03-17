package jacusa.worker;

import java.util.ArrayList;
import java.util.List;

import jacusa.method.pileup.PileupMethod;
import lib.data.ParallelData;
import lib.data.result.Result;
import lib.estimate.MinkaParameter;
import lib.stat.AbstractStat;
import lib.stat.GenericStat;
import lib.stat.estimation.provider.DeletionCountProvider;
import lib.stat.estimation.provider.InsertionCountProvider;
import lib.worker.AbstractWorker;

public class PileupWorker
extends AbstractWorker {
	
	private final AbstractStat stat;
	private final List<GenericStat> genericStats;
	
	public PileupWorker(final PileupMethod method, final int threadId) {
		super(method, threadId);
		stat = method.getParameter().getStatParameter()
				.newInstance(method.getParameter().getConditionsSize());

		genericStats = new ArrayList<GenericStat>(2);
		if (getParameter().showDeletionCount() ||
				getParameter().showInsertionCount() ||
				getParameter().showInsertionStartCount()) {
			final MinkaParameter minkaPrm = new MinkaParameter();
			if (getParameter().showDeletionCount()) {
				final DeletionCountProvider delCountProv = 
						new DeletionCountProvider(minkaPrm.getMaxIterations());
				genericStats.add(new GenericStat(minkaPrm, delCountProv, "deletion_score", "deletion_pvalue"));
			}
			if (getParameter().showInsertionCount() ||
					getParameter().showInsertionStartCount()) {
				final InsertionCountProvider insCountProv = 
						new InsertionCountProvider(minkaPrm.getMaxIterations());
				genericStats.add(new GenericStat(minkaPrm, insCountProv, "insertion_score", "insertion_pvalue"));
			}
		}
	}
	
	@Override
	protected Result process(final ParallelData parallelData) {
		Result result = stat.process(parallelData); 
		if (result == null) {
			return null;
		}
		
		// FIXME
		// TODO process INDELs
		
		return result;
	}
	
}
