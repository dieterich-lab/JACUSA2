package lib.data.fetcher.basecall;

import lib.cli.options.filter.has.HasReadSubstitution.BaseSubstitution;
import lib.data.DataContainer;
import lib.data.count.BaseSubstitution2BaseCallCount;
import lib.data.count.basecall.BaseCallCount;
import lib.data.fetcher.Fetcher;

public class BaseCallCountExtractor implements Fetcher<BaseCallCount> {

	private final BaseSubstitution baseSub;
	private final Fetcher<BaseSubstitution2BaseCallCount> fetcher;
	
	public BaseCallCountExtractor(final BaseSubstitution baseSub, final Fetcher<BaseSubstitution2BaseCallCount> fetcher) {
		this.baseSub = baseSub;
		this.fetcher = fetcher;
	}

	@Override
	public BaseCallCount fetch(DataContainer container) {
		return fetcher.fetch(container).get(baseSub);
	}

	public BaseSubstitution getBaseSubstitution() {
		return baseSub;
	}
	
}
