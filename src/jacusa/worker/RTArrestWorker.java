package jacusa.worker;

import jacusa.method.rtarrest.RTarrestMethod;
import lib.data.ParallelData;
import lib.data.result.Result;
import lib.stat.AbstractStat;
import lib.worker.AbstractWorker;

public class RTArrestWorker
extends AbstractWorker {

	private final AbstractStat stat;
	
	// TODO remove neved used private final Fetcher<BaseSub2BCC> bs2bccFetcher;
	
	public RTArrestWorker(final RTarrestMethod method, final int threadId) {
		super(method, threadId);
		stat = method.getParameter().getStatParameter()
				.newInstance(method.getParameter().getConditionsSize());
	
		/* TODO never used
		bs2bccFetcher = new BaseSub2BCCaggregator(
				Arrays.asList(
						DataType.ARREST_BASE_SUBST.getFetcher(), 
						DataType.THROUGH_BASE_SUBST.getFetcher()));
		*/
	}
	
	@Override
	protected Result process(final ParallelData parallelData) {
		Result result = stat.filter(parallelData); 
		if (result == null) {
			return null;
		}
		
		/* TODO remove never used
		final SortedSet<BaseSub> baseSubs = getParameter().getReadTags();
		if (! baseSubs.isEmpty()) {
			result = new BaseSubResult(baseSubs, bs2bccFetcher, result);
		}
		*/
		
		processGenericStats(result);
		
		return result;
	}
	
}
