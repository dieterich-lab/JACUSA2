package lib.data.cache;

import java.util.Arrays;

import lib.util.Coordinate;

import htsjdk.samtools.SAMRecord;

import lib.data.AbstractData;
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
	public AbstractData getData(final Coordinate coordinate) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void clear() {
		Arrays.fill(readStartCount, 0);
		Arrays.fill(readEndCount, 0);
	}

	public int getReadStartCount(final Coordinate coordinate) {
		final int windowPosition = Coordinate.makeRelativePosition(getActiveWindowCoordinates(), coordinate.getPosition());
		return readStartCount[windowPosition];
	}

	public int getReadEndCount(final Coordinate coordinate) {
		final int windowPosition = Coordinate.makeRelativePosition(getActiveWindowCoordinates(), coordinate.getPosition());
		return readEndCount[windowPosition];
	}
	
}
