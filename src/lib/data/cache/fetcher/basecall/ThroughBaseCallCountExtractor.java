package lib.data.cache.fetcher.basecall;

import lib.data.DataTypeContainer;
import lib.data.cache.fetcher.Fetcher;
import lib.data.cache.lrtarrest.ArrestPosition2baseCallCount;
import lib.data.count.basecall.BaseCallCount;

public class ThroughBaseCallCountExtractor implements Fetcher<BaseCallCount> {

	private final Fetcher<ArrestPosition2baseCallCount> pos2bccFetcher;
	
	public ThroughBaseCallCountExtractor(
			final Fetcher<ArrestPosition2baseCallCount> pos2bccFetcher) {
		this.pos2bccFetcher = pos2bccFetcher;
	}

	@Override
	public BaseCallCount fetch(DataTypeContainer container) {
		final int position = container.getCoordinate().getPosition();
		return pos2bccFetcher.fetch(container).getThroughBaseCallCount(position);
	}
	
}
