package lib.data.storage.basecall;

import lib.data.DataContainer;
import lib.data.count.basecall.BaseCallCount;
import lib.data.fetcher.Fetcher;
import lib.data.storage.container.SharedStorage;
import lib.data.stroage.AbstractStorage;
import lib.data.stroage.WindowCoverage;
import lib.util.Base;
import lib.util.coordinate.Coordinate;
import lib.util.coordinate.CoordinateUtil.STRAND;

public abstract class AbstractBaseCallCountStorage
extends AbstractStorage 
implements WindowCoverage {

	private final Fetcher<BaseCallCount> bccFetcher;
	
	public AbstractBaseCallCountStorage(
			final SharedStorage sharedStorage,
			final Fetcher<BaseCallCount> bccFetcher) {
		
		super(sharedStorage);
		this.bccFetcher = bccFetcher;
	}

	@Override
	public void populate(DataContainer container, int winPos, Coordinate coordinate) {
		if (bccFetcher == null) {
			return;
		}
		
		final BaseCallCount dest = bccFetcher.fetch(container);
		add(winPos, dest);
		if (coordinate.getStrand() == STRAND.REVERSE && dest.getCoverage() > 0) {
			dest.invert();
		}
	}
	
	void add(final int winPos, final BaseCallCount dest) {
		for (final Base base : Base.validValues()) {
			final int count = getCount(winPos, base);
			if (count > 0) {
				dest.set(base, count);
			}
		}
	}
	
	public abstract int getCount(final int winPos, final Base base);

	@Override
	public int getCoverage(int winPos) {
		int cov = 0;
		for (final Base base : Base.validValues()) {
			final int count = getCount(winPos, base);
			cov += count;
		}
		return cov;
	}
	
}
