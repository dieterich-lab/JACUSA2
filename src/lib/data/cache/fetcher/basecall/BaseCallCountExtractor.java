package lib.data.cache.fetcher.basecall;

import lib.cli.options.has.HasReadSubstitution.BaseSubstitution;
import lib.data.DataTypeContainer;
import lib.data.cache.fetcher.Fetcher;
import lib.data.count.BaseSubstitutionCount;
import lib.data.count.basecall.BaseCallCount;

public class BaseCallCountExtractor implements Fetcher<BaseCallCount> {

	private final BaseSubstitution baseSub;
	private final Fetcher<BaseSubstitutionCount> fetcher;
	
	public BaseCallCountExtractor(final BaseSubstitution baseSub, final Fetcher<BaseSubstitutionCount> fetcher) {
		this.baseSub = baseSub;
		this.fetcher = fetcher;
	}

	@Override
	public BaseCallCount fetch(DataTypeContainer container) {
		return fetcher.fetch(container).get(baseSub);
	}

	public BaseSubstitution getBaseSubstitution() {
		return baseSub;
	}
	
}
