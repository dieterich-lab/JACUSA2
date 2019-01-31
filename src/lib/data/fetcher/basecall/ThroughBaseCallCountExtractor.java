package lib.data.fetcher.basecall;

import lib.data.DataContainer;
import lib.data.count.basecall.BaseCallCount;
import lib.data.fetcher.Fetcher;
import lib.data.storage.lrtarrest.ArrestPosition2baseCallCount;

public class ThroughBaseCallCountExtractor implements Fetcher<BaseCallCount> {

	private final Fetcher<ArrestPosition2baseCallCount> pos2bccFetcher;
	
	public ThroughBaseCallCountExtractor(
			final Fetcher<ArrestPosition2baseCallCount> pos2bccFetcher) {
		this.pos2bccFetcher = pos2bccFetcher;
	}

	@Override
	public BaseCallCount fetch(DataContainer container) {
		final int onePosition = container.getCoordinate().get1Position();
		return pos2bccFetcher.fetch(container).getThroughBaseCallCount(onePosition);
	}
	
}
