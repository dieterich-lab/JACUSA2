package lib.data.cache;

import java.util.Arrays;

import lib.util.Coordinate;

import htsjdk.samtools.SAMRecord;

import lib.data.builder.SAMRecordWrapper;

// TODO do we consider read end by quality or by alignment
public class AlignmentCache extends AbstractCache {

	private final int[] readStartCount;
	private final int[] readEndCount;

	public AlignmentCache(final int activeWindowSize) {
		super(activeWindowSize);

		readStartCount = new int[activeWindowSize];
		readEndCount = new int[activeWindowSize];
	}

	@Override
	public void addRecordWrapper(final SAMRecordWrapper recordWrapper) {
		final SAMRecord record = recordWrapper.getSAMRecord();
		
		int windowPosition1 = Coordinate.makeRelativePosition(
				getActiveWindowCoordinates(), record.getAlignmentStart());
		if (windowPosition1 != -1) {
			readStartCount[windowPosition1]++;
		}
		
		int windowPosition2 = Coordinate.makeRelativePosition(
				getActiveWindowCoordinates(), record.getAlignmentEnd());
		if (windowPosition2 != -1) {
			readStartCount[windowPosition2]++;
		}
	}
	
	@Override
	public void clear() {
		Arrays.fill(readStartCount, 0);
		Arrays.fill(readEndCount, 0);
	}

	public int getReadStartCount(final Coordinate coordinate) {
		return readStartCount[0]; // TODO
	}

	public int getReadEndCount(final Coordinate coordinate) {
		return readEndCount[0]; // TODO
	}
	
}
