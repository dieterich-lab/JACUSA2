package lib.data.fetcher;

import java.util.List;

import lib.data.DataContainer;
import lib.data.count.BaseSub2BaseCallCount;

public class BaseSubstitution2BaseCallCountAggregator implements Fetcher<BaseSub2BaseCallCount> {

	private final List<Fetcher<BaseSub2BaseCallCount>> bs2bccFetchers;
	
	public BaseSubstitution2BaseCallCountAggregator(final List<Fetcher<BaseSub2BaseCallCount>> bs2bccFetchers) {
		this.bs2bccFetchers = bs2bccFetchers;
	}
	
	@Override
	public BaseSub2BaseCallCount fetch(DataContainer container) {
		final BaseSub2BaseCallCount totalBs2bcc = new BaseSub2BaseCallCount();
		for (final Fetcher<BaseSub2BaseCallCount> bs2bccFetcher : bs2bccFetchers) {
			totalBs2bcc.merge(bs2bccFetcher.fetch(container));
		}
		return totalBs2bcc;
	}

}
