package lib.data.cache.fetcher.basecall;

import lib.data.DataTypeContainer;
import lib.data.cache.fetcher.Fetcher;
import lib.data.cache.lrtarrest.Position2baseCallCount;
import lib.data.count.basecall.BaseCallCount;

public class ArrestBaseCallCountExtractor implements Fetcher<BaseCallCount> {

	private final Fetcher<Position2baseCallCount> fetcher;
	
	public ArrestBaseCallCountExtractor(final Fetcher<Position2baseCallCount> fetcher) {
		this.fetcher = fetcher;
	}

	// TODO position or total
	@Override
	public BaseCallCount fetch(DataTypeContainer container) {
		final int position = container.getCoordinate().getPosition();
		return fetcher.fetch(container).getBaseCallCount(position);
	}
	
}
