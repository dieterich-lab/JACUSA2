package lib.data.fetcher.basecall;

import lib.data.DataContainer;
import lib.data.count.basecall.BaseCallCount;
import lib.data.fetcher.Fetcher;
import lib.data.storage.lrtarrest.ArrestPos2BCC;

public class ThroughBaseCallCountExtractor implements Fetcher<BaseCallCount> {

	private final Fetcher<ArrestPos2BCC> pos2bccFetcher;
	
	public ThroughBaseCallCountExtractor(
			final Fetcher<ArrestPos2BCC> pos2bccFetcher) {
		this.pos2bccFetcher = pos2bccFetcher;
	}

	@Override
	public BaseCallCount fetch(DataContainer container) {
		final int onePosition = container.getCoordinate().get1Position();
		return pos2bccFetcher.fetch(container).getThroughBCC(onePosition);
	}
	
}
