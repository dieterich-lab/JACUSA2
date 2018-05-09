package lib.data.cache.region;

import java.util.Arrays;

import lib.util.coordinate.Coordinate;
import lib.util.coordinate.CoordinateController;
import lib.util.coordinate.CoordinateUtil.STRAND;

import lib.cli.options.BaseCallConfig;
import lib.data.AbstractData;
import lib.data.cache.extractor.basecall.BaseCallCountExtractor;
import lib.data.count.BaseCallCount;

public class ArrayBaseCallRegionDataCache<T extends AbstractData>
extends AbstractRestrictedRegionDataCache<T> {

	private final BaseCallCountExtractor<T> baseCallCountExtractor;
	
	private final int maxDepth;
	private final byte minBASQ;

	private final int[] coverage;
	private final int[][] baseCall;

	public ArrayBaseCallRegionDataCache(
			final int maxDepth, final byte minBASQ, 
			final BaseCallConfig baseCallConfig, 
			final CoordinateController coordinateController) {
		this(null, maxDepth, minBASQ, baseCallConfig, coordinateController);
	}
	
	public ArrayBaseCallRegionDataCache(
			final BaseCallCountExtractor<T> baseCallCountExtractor,
			final int maxDepth, final byte minBASQ, 
			final BaseCallConfig baseCallConfig, 
			final CoordinateController coordinateController) {

		super(baseCallConfig, coordinateController);

		this.baseCallCountExtractor = baseCallCountExtractor;
		
		this.maxDepth 	= maxDepth;
		this.minBASQ 	= minBASQ;

		coverage = new int[coordinateController.getActiveWindowSize()];
		baseCall = new int[coordinateController.getActiveWindowSize()][baseCallConfig.getBases().length];
	}
	
	@Override
	public void addData(T data, Coordinate coordinate) {
		final int windowPosition = getCoordinateController().convert2windowPosition(coordinate);
		if (getCoverage()[windowPosition] == 0) {
			return;
		}

		add(windowPosition, coordinate.getStrand(), baseCallCountExtractor.getBaseCallCount(data));
	}
	
	public void add(final int windowPosition, final STRAND strand, final BaseCallCount dest) {
		for (int baseIndex = 0; baseIndex < getBaseCallConfig().getBases().length; baseIndex++) {
			if (baseCall[windowPosition][baseIndex] > 0) {
				dest.set(baseIndex, baseCall[windowPosition][baseIndex]);
			}
		}
		
		if (strand == STRAND.REVERSE) {
			dest.invert();
		}
	}

	@Override
	public boolean isValid(final int windowPosition, final int readPosition, 
			final int baseIndex, final byte baseQuality) {

		// ensure max depth
		if (maxDepth > 0 && coverage[windowPosition] > maxDepth) {
			return false;
		}
		// ignore 'N' base calls
		if (baseIndex < 0) {
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
			final int baseIndex, final byte baseQuality) {

		coverage[windowPosition] 				+= 1;
		baseCall[windowPosition][baseIndex] 	+= 1;
	}

	@Override
	public void clear() {
		Arrays.fill(coverage, 0);
		for (int[] b : baseCall) {
			Arrays.fill(b, 0);	
		}
	}

	public int[] getCoverage() {
		return coverage;
	}
	
	public int[][] getBaseCalls() {
		return baseCall;
	}
	
	public int getMaxDepth() {
		return maxDepth;
	}
	
	public byte getMinBASQ() {
		return minBASQ;
	}

}
