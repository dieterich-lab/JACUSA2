package lib.data.fetcher.basecall;

import lib.data.DataContainer;
import lib.data.count.PileupCount;
import lib.data.count.basecall.BaseCallCount;
import lib.data.fetcher.Fetcher;

public class PileupCountBaseCallCountExtractor implements Fetcher<BaseCallCount>{

	private final Fetcher<PileupCount> pcFetcher;
	
	public PileupCountBaseCallCountExtractor(final Fetcher<PileupCount> pcFetcher) {
		this.pcFetcher = pcFetcher;
	}

	@Override
	public BaseCallCount fetch(DataContainer container) {
		return pcFetcher.fetch(container).getBCC();
	}
	
}
