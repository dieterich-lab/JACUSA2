package lib.data.fetcher.basecall;

import lib.data.DataContainer;
import lib.data.count.basecall.BaseCallCount;
import lib.data.fetcher.Fetcher;
import lib.data.storage.lrtarrest.ArrestPosition2baseCallCount;

public class ArrestBaseCallCountExtractor implements Fetcher<BaseCallCount> {

	private final Fetcher<ArrestPosition2baseCallCount> fetcher;
	
	public ArrestBaseCallCountExtractor(final Fetcher<ArrestPosition2baseCallCount> fetcher) {
		this.fetcher = fetcher;
	}

	@Override
	public BaseCallCount fetch(DataContainer container) {
		final int onePosition = container.getCoordinate().get1Position();
		return fetcher.fetch(container).getArrestBaseCallCount(onePosition);
	}
	
}
