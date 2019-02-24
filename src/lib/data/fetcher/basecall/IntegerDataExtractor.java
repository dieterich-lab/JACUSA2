package lib.data.fetcher.basecall;

import lib.cli.options.filter.has.HasReadSubstitution.BaseSubstitution;
import lib.data.DataContainer;
import lib.data.IntegerData;
import lib.data.count.BaseSubstitution2IntegerData;
import lib.data.fetcher.Fetcher;

public class IntegerDataExtractor implements Fetcher<IntegerData> {

	private final BaseSubstitution baseSub;
	private final Fetcher<BaseSubstitution2IntegerData> fetcher;
	
	public IntegerDataExtractor(final BaseSubstitution baseSub, final Fetcher<BaseSubstitution2IntegerData> fetcher) {
		this.baseSub = baseSub;
		this.fetcher = fetcher;
	}

	@Override
	public IntegerData fetch(DataContainer container) {
		return fetcher.fetch(container).get(baseSub);
	}

	public BaseSubstitution getBaseSubstitution() {
		return baseSub;
	}
	
}
