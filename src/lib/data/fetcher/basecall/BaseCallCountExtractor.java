package lib.data.fetcher.basecall;

import lib.cli.options.filter.has.BaseSub;
import lib.data.DataContainer;
import lib.data.count.BaseSub2BaseCallCount;
import lib.data.count.basecall.BaseCallCount;
import lib.data.fetcher.Fetcher;

public class BaseCallCountExtractor implements Fetcher<BaseCallCount> {

	private final BaseSub baseSub;
	private final Fetcher<BaseSub2BaseCallCount> fetcher;
	
	public BaseCallCountExtractor(final BaseSub baseSub, final Fetcher<BaseSub2BaseCallCount> fetcher) {
		this.baseSub = baseSub;
		this.fetcher = fetcher;
	}

	@Override
	public BaseCallCount fetch(DataContainer container) {
		return fetcher.fetch(container).get(baseSub);
	}

	public BaseSub getBaseSubstitution() {
		return baseSub;
	}
	
}
