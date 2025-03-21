package jacusa.worker;

import java.util.ArrayList;
import java.util.List;

import jacusa.method.pileup.PileupMethod;
import lib.data.ParallelData;
import lib.data.result.Result;
import lib.estimate.MinkaParameter;
import lib.stat.AbstractStat;
import lib.stat.INDELstat;
import lib.stat.estimation.provider.DeletionEstimateProvider;
import lib.stat.estimation.provider.InsertionEstimateProvider;
import lib.worker.AbstractWorker;

public class PileupWorker
extends AbstractWorker {
	
	private final AbstractStat stat;
	private final List<INDELstat> genericStats;
	
	public PileupWorker(final PileupMethod method, final int threadId) {
		super(method, threadId);
		stat = method.getParameter().getStatParameter()
				.newInstance(method.getParameter().getConditionsSize());

		genericStats = new ArrayList<INDELstat>(2);
		if (getParameter().showDeletionCount() ||
				getParameter().showInsertionCount() ||
				getParameter().showInsertionStartCount()) {
			final MinkaParameter minkaPrm = new MinkaParameter();
			if (getParameter().showDeletionCount()) {
				final DeletionEstimateProvider delCountProv = 
						new DeletionEstimateProvider(minkaPrm.getMaxIterations());
				genericStats.add(new INDELstat(minkaPrm, delCountProv, "deletion_score", "deletion_pvalue"));
			}
			if (getParameter().showInsertionCount() ||
					getParameter().showInsertionStartCount()) {
				final InsertionEstimateProvider insCountProv = 
						new InsertionEstimateProvider(minkaPrm.getMaxIterations());
				genericStats.add(new INDELstat(minkaPrm, insCountProv, "insertion_score", "insertion_pvalue"));
			}
		}
	}
	
	@Override
	protected Result process(final ParallelData parallelData) {
		Result result = stat.process(parallelData, null); 
		if (result == null) {
			return null;
		}
		
		// FIXME
		// TODO process INDELs
		
		return result;
	}
	
}
