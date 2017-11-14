package lib.data.cache;

import java.util.Arrays;

import lib.util.Coordinate;

import htsjdk.samtools.AlignmentBlock;
import htsjdk.samtools.SAMRecord;

import lib.cli.options.BaseCallConfig;
import lib.data.AbstractData;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.has.hasBaseCallCount;

public class BaseCallCache<T extends AbstractData & hasBaseCallCount> 
extends AbstractCache<T> {

	private final BaseCallConfig baseCallConfig;
	
	private final int maxDepth;
	private final byte minBASQ;
	
	private final int[] coverage;
	private final int[][] baseCalls;

	public BaseCallCache(final int maxDepth, final byte minBASQ, final BaseCallConfig baseCallConfig) {
		this.baseCallConfig = baseCallConfig;

		this.maxDepth = maxDepth;
		this.minBASQ = minBASQ;
		
		coverage = new int[getActiveWindowSize()];
		baseCalls = new int[getBaseSize()][getActiveWindowSize()];
	}

	@Override
	public void addRecordWrapper(final SAMRecordWrapper recordWrapper) {
		for (final AlignmentBlock alignmentBlock : recordWrapper.getSAMRecord().getAlignmentBlocks()) {
			addRecordWrapperRegion(alignmentBlock.getReadStart() - 1, alignmentBlock.getLength(), recordWrapper);
		}
	}
	
	@Override
	public void addRecordWrapperPosition(final int readPosition, final SAMRecordWrapper recordWrapper) {
		addRecordWrapperRegion(readPosition, 1, recordWrapper);
	}
	
	@Override
	public void addRecordWrapperRegion(final int readPosition, final int length, final SAMRecordWrapper recordWrapper) {
		final int referencePosition = recordWrapper.getSAMRecord().getReferencePositionAtReadPosition(readPosition);
		incrementBaseCalls(referencePosition, readPosition, length, recordWrapper);
	}

	@Override
	public void addData(T data, final Coordinate coordinate) {
		final int windowPosition = Coordinate.makeRelativePosition(getActiveWindowCoordinate(), coordinate.getPosition());
		if (coverage[windowPosition] == 0) {
			return;
		}

		for (int baseIndex = 0; baseIndex < getBaseSize(); baseIndex++) {
			data.getBaseCallCount().set(baseIndex, baseCalls[windowPosition][baseIndex]);
		}
	}

	protected void incrementBaseCalls(final int referencePosition, final int readPosition, int length, 
			final SAMRecordWrapper recordWrapper) {

		final WindowPosition windowPosition = WindowPosition.convert(
				getActiveWindowCoordinate(), referencePosition, readPosition, length);
		
		final SAMRecord record = recordWrapper.getSAMRecord();
		for (int j = 0; j < windowPosition.getLength(); ++j) {
			if (maxDepth > 0 && coverage[windowPosition.getWindowPosition() + j] > maxDepth) {
				continue;
			}
			final int baseIndex = baseCallConfig.getBaseIndex(record.getReadBases()[windowPosition.getRead() + j]);
			if (baseIndex < 0) {
				continue;
			}
			final byte bq = record.getBaseQualities()[windowPosition.getRead() + j];
			if (bq < minBASQ) {
				continue;
			}
			
			coverage[windowPosition.getWindowPosition() + j] += 1;
			baseCalls[windowPosition.getWindowPosition() + j][baseIndex] += 1;
		}
	}
	
	@Override
	public void clear() {
		Arrays.fill(coverage, 0);
		for (int[] b : baseCalls) {
			Arrays.fill(b, 0);	
		}
	}
	
	public int getCoverage(final int windowPosition) {
		return coverage[windowPosition];
	}
	
	public int getBaseCalls(final int baseIndex, final int windowPosition) {
		return baseCalls[baseIndex][windowPosition];
	}

	private int getBaseSize() {
		return baseCallConfig.getBases().length; 
	}
	
}
