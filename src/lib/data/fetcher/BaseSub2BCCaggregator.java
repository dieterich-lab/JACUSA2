package lib.data.fetcher;

import java.util.List;

import lib.data.DataContainer;
import lib.data.count.BaseSub2BCC;

public class BaseSub2BCCaggregator implements Fetcher<BaseSub2BCC> {

	private final List<Fetcher<BaseSub2BCC>> bs2bccFetchers;
	
	public BaseSub2BCCaggregator(final List<Fetcher<BaseSub2BCC>> bs2bccFetchers) {
		this.bs2bccFetchers = bs2bccFetchers;
	}
	
	@Override
	public BaseSub2BCC fetch(DataContainer container) {
		final BaseSub2BCC totalBs2bcc = new BaseSub2BCC();
		for (final Fetcher<BaseSub2BCC> bs2bccFetcher : bs2bccFetchers) {
			totalBs2bcc.merge(bs2bccFetcher.fetch(container));
		}
		return totalBs2bcc;
	}

}
