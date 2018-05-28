package lib.data.cache.region;

import java.util.HashMap;
import java.util.Map;

import htsjdk.samtools.util.SequenceUtil;
import lib.util.coordinate.Coordinate;
import lib.util.coordinate.CoordinateController;
import lib.util.coordinate.CoordinateUtil.STRAND;
import lib.cli.options.Base;
import lib.data.AbstractData;
import lib.data.basecall.map.MapBaseCallCount;
import lib.data.cache.extractor.basecall.BaseCallCountExtractor;
import lib.data.count.BaseCallCount;

public class MapBaseCallRegionDataCache<X extends AbstractData>
extends AbstractRestrictedRegionDataCache<X> {

	private final BaseCallCountExtractor<X> baseCallCountExtractor;
	
	private final int maxDepth;
	private final byte minBASQ;

	private final Map<Integer, Integer> winPos2coverage;
	private final Map<Integer, BaseCallCount> winPos2baseCallCount;

	public MapBaseCallRegionDataCache(
			final int maxDepth, final byte minBASQ, 
			final CoordinateController coordinateController) {
		this(null, maxDepth, minBASQ, coordinateController);
	}
	
	public MapBaseCallRegionDataCache(
			final BaseCallCountExtractor<X> baseCallCountExtractor,
			final int maxDepth, final byte minBASQ, 
			final CoordinateController coordinateController) {

		super(coordinateController);

		this.baseCallCountExtractor = baseCallCountExtractor;
		
		this.maxDepth 	= maxDepth;
		this.minBASQ 	= minBASQ;

		final int n = coordinateController.getActiveWindowSize();
		winPos2coverage = new HashMap<Integer, Integer>(n);
		winPos2baseCallCount = new HashMap<Integer, BaseCallCount>(n);
	}
	
	@Override
	public void addData(X data, Coordinate coordinate) {
		final int windowPosition = getCoordinateController().convert2windowPosition(coordinate);
		if (getCoverage(windowPosition) == 0) {
			return;
		}

		add(windowPosition, coordinate.getStrand(), baseCallCountExtractor.getBaseCallCount(data));
	}
	
	protected void add(final int windowPosition, final STRAND strand, final BaseCallCount dest) {
		dest.add(winPos2baseCallCount.get(windowPosition));
		if (strand == STRAND.REVERSE) {
			dest.invert();
		}		
	}

	@Override
	public boolean isValid(final int windowPosition, final int readPosition, 
			final Base base, final byte baseQuality) {

		
		// ignore 'N' base calls
		if (! SequenceUtil.isValidBase(base.getC())) {
			return false;
		}
		// ensure max depty
		if (maxDepth > 0 && winPos2coverage.get(windowPosition) > maxDepth) {
			return false;
		}
		// ensure base quality
		if (baseQuality < minBASQ) {
			return false;
		}

		return true;
	}

	@Override
	public void increment(final int windowPosition, final int readPosition, 
			final Base base, final byte baseQuality) {

		int count = 0;
		if (winPos2coverage.containsKey(windowPosition)) {
			count = winPos2coverage.get(windowPosition);
			winPos2baseCallCount.put(windowPosition, new MapBaseCallCount());
		}
		winPos2coverage.put(windowPosition, count + 1);
		winPos2baseCallCount.get(windowPosition).increment(base);
	}

	@Override
	public void clear() {
		winPos2coverage.clear();
		winPos2baseCallCount.clear();
	}

	public int getCoverage(final int winPos) {
		return winPos2coverage.containsKey(winPos) ? winPos2coverage.get(winPos) : 0;
	}
	
	public BaseCallCount getBaseCallCount(final int winPos) {
		return winPos2baseCallCount.get(winPos);
	}
	
	public int getMaxDepth() {
		return maxDepth;
	}
	
	public byte getMinBASQ() {
		return minBASQ;
	}
	
}
