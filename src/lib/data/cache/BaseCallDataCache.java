package lib.data.cache;

import java.util.Arrays;

import lib.tmp.CoordinateController;
import lib.tmp.CoordinateController.WindowPositionGuard;
import lib.util.coordinate.Coordinate;
import lib.util.coordinate.CoordinateUtil.STRAND;

import htsjdk.samtools.AlignmentBlock;
import htsjdk.samtools.SAMRecord;

import lib.cli.options.BaseCallConfig;
import lib.data.AbstractData;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.has.hasBaseCallCount;

public class BaseCallDataCache<T extends AbstractData & hasBaseCallCount>
extends AbstractDataCache<T> {

	private final BaseCallConfig baseCallConfig;
	
	private final int maxDepth;
	private final byte minBASQ;
	
	private final int[] coverage;
	private final int[][] baseCalls;
	
	public BaseCallDataCache(final int maxDepth, final byte minBASQ, final BaseCallConfig baseCallConfig, final CoordinateController coordinateController) {
		super(coordinateController);
		this.baseCallConfig = baseCallConfig;

		this.maxDepth = maxDepth;
		this.minBASQ = minBASQ;
		
		coverage = new int[coordinateController.getActiveWindowSize()];
		baseCalls = new int[coordinateController.getActiveWindowSize()][getBaseSize()];
	}

	@Override
	public void addRecordWrapper(final SAMRecordWrapper recordWrapper) {
		for (final AlignmentBlock alignmentBlock : recordWrapper.getSAMRecord().getAlignmentBlocks()) {
			incrementBaseCalls(alignmentBlock.getReferenceStart(), alignmentBlock.getReadStart() - 1, alignmentBlock.getLength(), recordWrapper);
		}
	}
	
	protected void incrementBaseCalls(final int referencePosition, final int readPosition, int length, 
			final SAMRecordWrapper recordWrapper) {

		if (referencePosition < 0) {
			throw new IllegalArgumentException("Reference Position cannot be < 0! -> outside of alignmentBlock");
		}
		
		final WindowPositionGuard windowPositionGuard = getCoordinateController().convert(referencePosition, readPosition, length);
		
		if (windowPositionGuard.getWindowPosition() < 0 && windowPositionGuard.getLength() > 0) {
			throw new IllegalArgumentException("Window position cannot be < 0! -> outside of alignmentBlock");
		}
		
		final SAMRecord record = recordWrapper.getSAMRecord();
		for (int j = 0; j < windowPositionGuard.getLength(); ++j) {
			if (maxDepth > 0 && coverage[windowPositionGuard.getWindowPosition() + j] > maxDepth) {
				continue;
			}
			final int baseIndex = baseCallConfig.getBaseIndex(record.getReadBases()[windowPositionGuard.getReadPosition() + j]);
			if (baseIndex < 0) {
				continue;
			}
			final byte bq = record.getBaseQualities()[windowPositionGuard.getReadPosition() + j];
			if (bq < minBASQ) {
				continue;
			}

			incrementBaseCall(windowPositionGuard.getWindowPosition() + j,
					windowPositionGuard.getReadPosition() + j,
					baseIndex,
					bq);
		}
	}
	
	protected void incrementBaseCall(final int windowPosition, final int readPosition, final int baseIndex, final byte bq) {
		coverage[windowPosition] += 1;
		baseCalls[windowPosition][baseIndex] += 1;
	}
	
	@Override
	public void addData(T data, Coordinate coordinate) {
		final int windowPosition = getCoordinateController().convert2windowPosition(coordinate);
		if (getCoverage()[windowPosition] == 0) {
			return;
		}

		System.arraycopy(getBaseCalls()[windowPosition], 0, 
				data.getBaseCallCount().getBaseCallCount(), 0, getBaseSize());

		if (coordinate.getStrand() == STRAND.REVERSE) {
			data.getBaseCallCount().invert();
		}
		
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
