package lib.data.cache.fetcher.basecall;

import lib.data.DataTypeContainer;
import lib.data.cache.fetcher.Fetcher;
import lib.data.cache.lrtarrest.Position2baseCallCount;
import lib.data.count.basecall.BaseCallCount;
import lib.data.count.basecall.UnmodifiableBaseCallCount;

public class ThroughBaseCallCountExtractor implements Fetcher<BaseCallCount> {

	private final Fetcher<BaseCallCount> bccFetcher;
	private final Fetcher<Position2baseCallCount> pos2bccFetcher;
	
	public ThroughBaseCallCountExtractor(
			final Fetcher<BaseCallCount> bccFetcher,
			final Fetcher<Position2baseCallCount> pos2bccFetcher) {
		this.bccFetcher = bccFetcher;
		this.pos2bccFetcher = pos2bccFetcher;
	}

	@Override
	public BaseCallCount fetch(DataTypeContainer container) {
		final int position = container.getCoordinate().getPosition();
		final BaseCallCount bcc = bccFetcher.fetch(container).copy();
		bcc.subtract(
				pos2bccFetcher.fetch(container).getBaseCallCount(position));
		return new UnmodifiableBaseCallCount(bcc);
	}
	
}
