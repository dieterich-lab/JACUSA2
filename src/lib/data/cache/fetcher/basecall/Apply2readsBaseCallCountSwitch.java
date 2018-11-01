package lib.data.cache.fetcher.basecall;

import java.util.Set;

import jacusa.method.rtarrest.RTarrestMethod.RT_READS;
import lib.data.DataTypeContainer;
import lib.data.cache.fetcher.Fetcher;
import lib.data.count.basecall.BaseCallCount;

public class Apply2readsBaseCallCountSwitch implements Fetcher<BaseCallCount> {

	private final Set<RT_READS> apply2reads;
	
	private final Fetcher<BaseCallCount> totalFetcher;
	private final Fetcher<BaseCallCount> arrestBccFetcher;
	private final Fetcher<BaseCallCount> throughBccFetcher;
	
	public Apply2readsBaseCallCountSwitch(
			final Set<RT_READS> apply2reads,
			final Fetcher<BaseCallCount> totalBccFetcher,
			final Fetcher<BaseCallCount> arrestBccFetcher,
			final Fetcher<BaseCallCount> throughBccFetcher) {
		
		this.apply2reads 		= apply2reads;
		this.totalFetcher 		= totalBccFetcher;
		this.arrestBccFetcher 	= arrestBccFetcher;
		this.throughBccFetcher 	= throughBccFetcher;
	}

	@Override
	public BaseCallCount fetch(DataTypeContainer container) {
		if (apply2reads.size() == 2) {
			return totalFetcher.fetch(container);
		}
		
		if (apply2reads.contains(RT_READS.ARREST)) {
			return arrestBccFetcher.fetch(container);
		} else if (apply2reads.contains(RT_READS.THROUGH)) {
			return throughBccFetcher.fetch(container);	
		} else {
			throw new IllegalStateException("apply2reads cannot be empty!");
		}
	}
	
	public Set<RT_READS> getApply2reads() {
		return apply2reads;
	}
	
}
