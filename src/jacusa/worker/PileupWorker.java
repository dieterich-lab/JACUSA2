package jacusa.worker;


import java.util.ArrayList;
import java.util.List;

import jacusa.method.pileup.PileupMethod;
import lib.data.ParallelData;
import lib.data.result.Result;
import lib.estimate.MinkaParameter;
import lib.stat.AbstractStat;
import lib.stat.GenericStat;
import lib.stat.estimation.provider.DeletionEstCountProvider;
import lib.stat.estimation.provider.InsertionEstCountProvider;
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
				final DeletionEstCountProvider delCountProv = 
						new DeletionEstCountProvider(minkaPrm.getMaxIterations());
				genericStats.add(new GenericStat(minkaPrm, delCountProv, "deletion_score", "deletion_pvalue"));
			}
			if (getParameter().showInsertionCount() ||
					getParameter().showInsertionStartCount()) {
				final InsertionEstCountProvider insCountProv = 
						new InsertionEstCountProvider(minkaPrm.getMaxIterations());
				genericStats.add(new GenericStat(minkaPrm, insCountProv, "insertion_score", "insertion_pvalue"));
			}
		}
	}
	
	@Override
	protected Result process(final ParallelData parallelData) {
		Result result = stat.filter(parallelData); 
		if (result == null) {
			return null;
		}

		/* TODO neved used
		final SortedSet<BaseSub> baseSubs = getParameter().getReadTags();
		if (! baseSubs.isEmpty()) {
			result = new BaseSubResult(baseSubs, DataType.BASE_SUBST2BCC.getFetcher(), result);
		}
		*/

		processGenericStats(result);
		
		return result;
	}
	
}
