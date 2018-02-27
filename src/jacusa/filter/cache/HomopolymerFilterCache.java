package jacusa.filter.cache;

import java.util.Arrays;

import htsjdk.samtools.AlignmentBlock;
import htsjdk.samtools.SAMRecord;
import lib.cli.options.BaseCallConfig;
import lib.data.AbstractData;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.has.hasHomopolymerInfo;
import lib.util.coordinate.CoordinateController;
import lib.util.coordinate.CoordinateController.WindowPositionGuard;
import lib.util.coordinate.Coordinate;

public class HomopolymerFilterCache<T extends AbstractData & hasHomopolymerInfo> 
extends AbstractFilterCache<T> {
	
	private final int minLength;

	private final BaseCallConfig baseCallConfig;
	private final CoordinateController coordinateController;
	
	private final boolean[] isHomopolymer;
	
	/**
	 * 
	 * @param c
	 * @param distance
	 */
	public HomopolymerFilterCache(final char c, final int length, final BaseCallConfig baseCallConfig, final CoordinateController coordinateController) {
		super(c);
		this.minLength = length;
		this.baseCallConfig = baseCallConfig;
		this.coordinateController = coordinateController;
		isHomopolymer = new boolean[coordinateController.getActiveWindowSize()];
	}

	public int getOverhang() {
		return minLength;
	}

	@Override
	public void addRecordWrapper(final SAMRecordWrapper recordWrapper) {
		for (AlignmentBlock block : recordWrapper.getSAMRecord().getAlignmentBlocks()) {
			final int referencePosition = block.getReferenceStart();
			final int readPosition = block.getReadStart() - 1;
			final int length = block.getLength();
			
			if (length >= minLength) {
				processAlignmentBlock(referencePosition, readPosition, length, recordWrapper);
			}
		}
	}
	
	private void processAlignmentBlock(final int referencePosition, final int readPosition, final int length, final SAMRecordWrapper recordWrapper) {
		if (referencePosition < 0) {
			throw new IllegalArgumentException("Reference Position cannot be < 0! -> outside of alignmentBlock");
		}
		
		final WindowPositionGuard windowPositionGuard = getCoordinateController().convert(referencePosition, readPosition, length);
		
		if (windowPositionGuard.getWindowPosition() < 0 && windowPositionGuard.getLength() > 0) {
			throw new IllegalArgumentException("Window position cannot be < 0! -> outside of alignmentBlock");
		}
		
		final SAMRecord record = recordWrapper.getSAMRecord();
		
		int polymerLength = 0;
		int firstReferencePosition = -1;
		int lastBaseIndex = -1;

		for (int j = 0; j < windowPositionGuard.getLength(); ++j) {
			final int baseIndex = baseCallConfig.getBaseIndex(record.getReadBases()[windowPositionGuard.getReadPosition() + j]);
			if (baseIndex < 0) {
				if (polymerLength >= minLength) {
					markRegion(firstReferencePosition, polymerLength);
				}
				polymerLength = 0;
				firstReferencePosition = -1;
				lastBaseIndex = -1;
			} else if (baseIndex == lastBaseIndex) {
				polymerLength++;
			} else {
				if (polymerLength >= minLength) {
					markRegion(firstReferencePosition, polymerLength);
				}
				polymerLength = 1;
				firstReferencePosition = windowPositionGuard.getReferencePosition() + j;
				lastBaseIndex = baseIndex;
			}
		}
	}

	private void markRegion(final int firstReferencePosition, final int length) {
		final int windowPosition = coordinateController.convert2windowPosition(firstReferencePosition);
		for (int i = 0; i < length; ++i) {
			isHomopolymer[windowPosition + i] = true;
		}
	}
	
	@Override
	public void clear() {
		Arrays.fill(isHomopolymer, false);
	}

	@Override
	public void addData(final T data, final Coordinate coordinate) {
		final int windowPosition = getCoordinateController().convert2windowPosition(coordinate);
		if (windowPosition < 0) {
			return;
		}

		data.setHomopolymer(isHomopolymer[windowPosition]);
	}

	@Override
	public CoordinateController getCoordinateController() {
		return coordinateController;
	}
	
	
}