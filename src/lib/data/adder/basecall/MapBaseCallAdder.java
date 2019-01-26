package lib.data.adder.basecall;

import java.util.HashMap;
import java.util.Map;

import htsjdk.samtools.SAMRecord;
import jacusa.JACUSA;
import lib.util.Base;
import lib.util.coordinate.Coordinate;
import lib.util.coordinate.CoordinateUtil.STRAND;
import lib.data.DataTypeContainer;
import lib.data.adder.AbstractDataContainerPopulator;
import lib.data.adder.IncrementAdder;
import lib.data.cache.container.SharedCache;
import lib.data.cache.fetcher.Fetcher;
import lib.data.count.basecall.BaseCallCount;

public class MapBaseCallAdder
extends AbstractDataContainerPopulator 
implements IncrementAdder {

	private final Fetcher<BaseCallCount> bccFetcher;
	
	private final Map<Integer, Integer> winPos2coverage;
	private final Map<Integer, BaseCallCount> winPos2baseCallCount;

	public MapBaseCallAdder(
			final Fetcher<BaseCallCount> baseCallCountFetcher,
			final SharedCache sharedCache) {

		super(sharedCache);

		this.bccFetcher = baseCallCountFetcher;

		final int n 			= getCoordinateController().getActiveWindowSize();
		winPos2coverage 		= new HashMap<Integer, Integer>(n);
		winPos2baseCallCount 	= new HashMap<Integer, BaseCallCount>(n);
	}
	
	@Override
	public void populate(DataTypeContainer container, Coordinate coordinate) {
		final int windowPosition = getCoordinateController().getCoordinateTranslator().coordinate2windowPosition(coordinate);
		if (getCoverage(windowPosition) == 0) {
			return;
		}

		add(windowPosition, coordinate.getStrand(), bccFetcher.fetch(container));
	}
	
	protected void add(final int windowPosition, final STRAND strand, final BaseCallCount dest) {
		dest.add(winPos2baseCallCount.get(windowPosition));
		if (strand == STRAND.REVERSE) {
			dest.invert();
		}		
	}

	@Override
	public void increment(final int referencePosition, final int windowPosition, final int readPosition, 
			final Base base, final byte baseQuality,
			final SAMRecord record) {

		int count = 0;
		if (! winPos2coverage.containsKey(windowPosition)) {
			winPos2baseCallCount.put(windowPosition, JACUSA.BCC_FACTORY.create());
		} else {
			count = winPos2coverage.get(windowPosition);
		}
		winPos2coverage.put(windowPosition, count + 1);
		winPos2baseCallCount.get(windowPosition).increment(base);
	}

	@Override
	public void clear() {
		winPos2coverage.clear();
		winPos2baseCallCount.clear();
	}

	@Override
	public int getCoverage(final int windowPosition) {
		return winPos2coverage.containsKey(windowPosition) ? winPos2coverage.get(windowPosition) : 0;
	}
	
}
