package lib.data.fetcher.basecall;

import lib.data.DataContainer;
import lib.data.count.basecall.BaseCallCount;
import lib.data.fetcher.Fetcher;
import lib.data.storage.lrtarrest.ArrestPos2BCC;

public class ArrestBaseCallCountExtractor implements Fetcher<BaseCallCount> {

	private final Fetcher<ArrestPos2BCC> fetcher;
	
	public ArrestBaseCallCountExtractor(final Fetcher<ArrestPos2BCC> fetcher) {
		this.fetcher = fetcher;
	}

	@Override
	public BaseCallCount fetch(DataContainer container) {
		final int onePosition = container.getCoordinate().get1Position();
		return fetcher.fetch(container).getArrestBCC(onePosition);
	}
	
}
