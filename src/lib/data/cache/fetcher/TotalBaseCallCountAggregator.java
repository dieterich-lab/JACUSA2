package lib.data.cache.fetcher;

import java.util.List;

import jacusa.JACUSA;
import lib.data.DataTypeContainer;
import lib.data.count.basecall.BaseCallCount;

public class TotalBaseCallCountAggregator implements Fetcher<BaseCallCount> {

	private final List<Fetcher<BaseCallCount>> bccFetchers;
	
	public TotalBaseCallCountAggregator(final List<Fetcher<BaseCallCount>> bccFetchers) {
		this.bccFetchers = bccFetchers;
	}
	
	@Override
	public BaseCallCount fetch(DataTypeContainer container) {
		final BaseCallCount totalBcc = JACUSA.bccFactory.create();
		for (final Fetcher<BaseCallCount> bccFetcher : bccFetchers) {
			totalBcc.add(bccFetcher.fetch(container));
		}
		return totalBcc;
	}

}
