package lib.data.fetcher.basecall;

import lib.cli.options.filter.has.BaseSub;
import lib.data.DataContainer;
import lib.data.IntegerData;
import lib.data.count.BaseSub2IntData;
import lib.data.fetcher.Fetcher;

public class IntegerDataExtractor implements Fetcher<IntegerData> {

	private final BaseSub baseSub;
	private final Fetcher<BaseSub2IntData> fetcher;
	
	public IntegerDataExtractor(final BaseSub baseSub, final Fetcher<BaseSub2IntData> fetcher) {
		this.baseSub = baseSub;
		this.fetcher = fetcher;
	}

	@Override
	public IntegerData fetch(DataContainer container) {
		return fetcher.fetch(container).get(baseSub);
	}

	public BaseSub getBaseSub() {
		return baseSub;
	}
	
}
