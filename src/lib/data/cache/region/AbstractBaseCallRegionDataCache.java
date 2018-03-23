package lib.data.cache.region;

import java.util.Arrays;

import lib.util.coordinate.CoordinateController;
import lib.util.coordinate.CoordinateController.WindowPositionGuard;
import lib.util.coordinate.CoordinateUtil.STRAND;

import htsjdk.samtools.SAMRecord;

import lib.cli.options.BaseCallConfig;
import lib.data.AbstractData;
import lib.data.BaseCallCount;
import lib.data.builder.recordwrapper.SAMRecordWrapper;

public abstract class AbstractBaseCallRegionDataCache<T extends AbstractData>
extends AbstractRegionDataCache<T> {

	private final BaseCallConfig baseCallConfig;

	private final int maxDepth;
	private final byte minBASQ;

	private final int[] coverage;
	private final int[][] baseCalls;

	public AbstractBaseCallRegionDataCache(final int maxDepth, final byte minBASQ, 
			final BaseCallConfig baseCallConfig, 
			final CoordinateController coordinateController) {

		super(coordinateController);
		this.baseCallConfig = baseCallConfig;

		this.maxDepth = maxDepth;
		this.minBASQ = minBASQ;
		
		coverage = new int[coordinateController.getActiveWindowSize()];
		baseCalls = new int[coordinateController.getActiveWindowSize()][getBaseSize()];
	}

	@Override
	public void addRecordWrapperRegion(final int referencePosition, final int readPosition, final int length, final SAMRecordWrapper recordWrapper) {
		if (referencePosition < 0) {
			throw new IllegalArgumentException("Reference Position cannot be < 0! -> outside of alignmentBlock");
		}
		
		final WindowPositionGuard windowPositionGuard = getCoordinateController().convert(referencePosition, readPosition, length);
		
		if (windowPositionGuard.getWindowPosition() < 0 && windowPositionGuard.getLength() > 0) {
			throw new IllegalArgumentException("Window position cannot be < 0! -> outside of alignmentBlock");
		}

		final SAMRecord record = recordWrapper.getSAMRecord();
		for (int j = 0; j < windowPositionGuard.getLength(); ++j) {
			final int baseIndex = baseCallConfig.getBaseIndex(record.getReadBases()[windowPositionGuard.getReadPosition() + j]);
			final byte bq = record.getBaseQualities()[windowPositionGuard.getReadPosition() + j];
			if (isValid(windowPositionGuard.getWindowPosition() + j, windowPositionGuard.getReadPosition() + j , baseIndex, bq)) {
				incrementBaseCall(windowPositionGuard.getWindowPosition() + j,
						windowPositionGuard.getReadPosition() + j,
						baseIndex,
						bq);	
			}
		}
	}

	protected void add(final int windowPosition, final STRAND strand, final BaseCallCount baseCallCount) {
		System.arraycopy(getBaseCalls()[windowPosition], 0, 
				baseCallCount.getBaseCallCount(), 0, getBaseSize());
		if (strand == STRAND.REVERSE) {
			baseCallCount.invert();
		}		
	}
	
	public boolean isValid(final int windowPosition, final int readPosition, final int baseIndex, final byte bq) {
		// check max covearge
		if (maxDepth > 0 && coverage[windowPosition] > maxDepth) {
			return false;
		}
		// check non N base call
		if (baseIndex < 0) {
			return false;
		}
		// check base call qualitry
		if (bq < minBASQ) {
			return false;
		}
		
		return true;
	}

	public void incrementBaseCall(final int windowPosition, final int readPosition, final int baseIndex, final byte bq) {
		// System.out.println(windowPosition + "\t" + readPosition + "\t" + baseIndex);
		coverage[windowPosition] += 1;
		baseCalls[windowPosition][baseIndex] += 1;
	}

	@Override
	public void clear() {
		Arrays.fill(coverage, 0);
		for (int[] b : baseCalls) {
			Arrays.fill(b, 0);	
		}
	}

	public int[] getCoverage() {
		return coverage;
	}
	
	public int[][] getBaseCalls() {
		return baseCalls;
	}

	public int getBaseSize() {
		return baseCallConfig.getBases().length; 
	}
	
	public byte getMaxBaseCallQuality() {
		return baseCallConfig.getMaxBaseCallQuality();
	}

	public byte getMinBaseCallQuality() {
		return minBASQ;
	}
	
	public BaseCallConfig getBaseCallConfig() {
		return baseCallConfig;
	}
	
	public int getMaxDepth() {
		return maxDepth;
	}
	
}
