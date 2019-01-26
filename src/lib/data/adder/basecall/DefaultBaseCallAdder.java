package lib.data.adder.basecall;

import java.util.Arrays;

import htsjdk.samtools.SAMRecord;
import lib.util.Base;
import lib.util.coordinate.Coordinate;
import lib.util.coordinate.CoordinateUtil.STRAND;
import lib.data.DataTypeContainer;
import lib.data.adder.AbstractDataContainerPopulator;
import lib.data.adder.IncrementAdder;
import lib.data.cache.container.SharedCache;
import lib.data.cache.fetcher.Fetcher;
import lib.data.count.basecall.BaseCallCount;

public class DefaultBaseCallAdder
extends AbstractDataContainerPopulator 
implements IncrementAdder {

	private final int[] bcA;
	private final int[] bcC;
	private final int[] bcG;
	private final int[] bcT;
	
	private final Fetcher<BaseCallCount> bccFetcher;
	
	public DefaultBaseCallAdder(
			final SharedCache sharedCache,
			final Fetcher<BaseCallCount> bccFetcher) {

		super(sharedCache);
		this.bccFetcher = bccFetcher;
		bcA = new int[getCoordinateController().getActiveWindowSize()];
		bcC = new int[getCoordinateController().getActiveWindowSize()];
		bcG = new int[getCoordinateController().getActiveWindowSize()];
		bcT = new int[getCoordinateController().getActiveWindowSize()];
	}
	
	@Override
	public void populate(DataTypeContainer container, Coordinate coordinate) {
		if (bccFetcher == null) {
			return;
		}
		
		final int windowPosition = getCoordinateController().getCoordinateTranslator().coordinate2windowPosition(coordinate);
		if (getCoverage(windowPosition) == 0) {
			return;
		}

		final BaseCallCount dest = bccFetcher.fetch(container);
		add(windowPosition, coordinate.getStrand(), dest);
	}
	
	public void add(final int windowPosition, final STRAND strand, final BaseCallCount dest) {
		for (final Base base : Base.validValues()) {
			final int[] tmpBc = getBaseCallStorage(base);
			if (tmpBc[windowPosition] > 0) {
				dest.set(base, tmpBc[windowPosition]);
			}
		}
		
		if (strand == STRAND.REVERSE) {
			dest.invert();
		}
	}

	@Override
	public void increment(final int referencePosition, final int windowPosition, final int readPosition, 
			final Base base, final byte baseQuality,
			final SAMRecord record) {

		if (base != Base.N) {
			getBaseCallStorage(base)[windowPosition] += 1;
		}
	}

	private int[] getBaseCallStorage(final Base base) {
		switch (base) {
		
		case A:
			return bcA;
			
		case C:
			return bcC;
			
		case G:
			return bcG;
			
		case T:
			return bcT;
		
		default:
			throw new IllegalStateException();
		}
	}
	
	@Override
	public void clear() {
		for (final Base base : Base.validValues()) {
			Arrays.fill(getBaseCallStorage(base), 0);	
		}
	}

	@Override
	public int getCoverage(final int windowPosition) {
		int coverage = 0;
		for (final Base base : Base.validValues()) {
			coverage += getBaseCallStorage(base)[windowPosition];
		}
		return coverage;
	}

}
