package lib.data.cache.fetcher.basecall;

import lib.data.DataTypeContainer;
import lib.data.cache.fetcher.Fetcher;
import lib.data.count.PileupCount;
import lib.data.count.basecall.BaseCallCount;

public class PileupCountBaseCallCountExtractor implements Fetcher<BaseCallCount>{

	private final Fetcher<PileupCount> pcFetcher;
	
	public PileupCountBaseCallCountExtractor(final Fetcher<PileupCount> pcFetcher) {
		this.pcFetcher = pcFetcher;
	}

	@Override
	public BaseCallCount fetch(DataTypeContainer container) {
		return pcFetcher.fetch(container).getBaseCallCount();
	}
	
}
