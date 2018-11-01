package lib.data.cache.fetcher.basecall;

import lib.data.DataTypeContainer;
import lib.data.cache.fetcher.Fetcher;
import lib.data.cache.lrtarrest.ArrestPos2BaseCallCount;
import lib.data.count.basecall.BaseCallCount;

public class ThroughBaseCallCountExtractor implements Fetcher<BaseCallCount> {

	private final Fetcher<ArrestPos2BaseCallCount> fetcher;
	
	public ThroughBaseCallCountExtractor(final Fetcher<ArrestPos2BaseCallCount> fetcher) {
		this.fetcher = fetcher;
	}

	@Override
	public BaseCallCount fetch(DataTypeContainer container) {
		final int position = container.getCoordinate().getPosition();
		return fetcher.fetch(container).getThroughBaseCallCount(position);
	}
	
}
