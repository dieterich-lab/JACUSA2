package lib.data.cache.fetcher.basecall;

import lib.data.DataTypeContainer;
import lib.data.cache.fetcher.Fetcher;
import lib.data.cache.lrtarrest.ArrestPosition2baseCallCount;
import lib.data.count.basecall.BaseCallCount;

public class ArrestBaseCallCountExtractor implements Fetcher<BaseCallCount> {

	private final Fetcher<ArrestPosition2baseCallCount> fetcher;
	
	public ArrestBaseCallCountExtractor(final Fetcher<ArrestPosition2baseCallCount> fetcher) {
		this.fetcher = fetcher;
	}

	@Override
	public BaseCallCount fetch(DataTypeContainer container) {
		final int position = container.getCoordinate().getPosition();
		return fetcher.fetch(container).getArrestBaseCallCount(position);
	}
	
}
