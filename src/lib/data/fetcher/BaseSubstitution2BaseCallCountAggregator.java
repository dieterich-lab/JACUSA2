package lib.data.fetcher;

import java.util.List;

import lib.data.DataContainer;
import lib.data.count.BaseSubstitution2BaseCallCount;

public class BaseSubstitution2BaseCallCountAggregator implements Fetcher<BaseSubstitution2BaseCallCount> {

	private final List<Fetcher<BaseSubstitution2BaseCallCount>> bs2bccFetchers;
	
	public BaseSubstitution2BaseCallCountAggregator(final List<Fetcher<BaseSubstitution2BaseCallCount>> bs2bccFetchers) {
		this.bs2bccFetchers = bs2bccFetchers;
	}
	
	@Override
	public BaseSubstitution2BaseCallCount fetch(DataContainer container) {
		final BaseSubstitution2BaseCallCount totalBs2bcc = new BaseSubstitution2BaseCallCount();
		for (final Fetcher<BaseSubstitution2BaseCallCount> bs2bccFetcher : bs2bccFetchers) {
			totalBs2bcc.merge(bs2bccFetcher.fetch(container));
		}
		return totalBs2bcc;
	}

}
