package lib.data.cache;

import java.util.Arrays;

import lib.util.Coordinate;

import htsjdk.samtools.SAMRecord;

import lib.data.AbstractData;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.has.hasBaseCallCount;
import lib.data.has.hasReadInfoCount;
import lib.data.has.hasLibraryType.LIBRARY_TYPE;

// TODO do we consider read end by quality or by alignment
public class AlignmentCache<T extends AbstractData & hasBaseCallCount & hasReadInfoCount> 
extends AbstractCache<T> {

	private final LIBRARY_TYPE libraryType;

	private final int[] readStartCount;
	private final int[] readEndCount;

	public AlignmentCache(final LIBRARY_TYPE libraryType, final int activeWindowSize) {
		super(activeWindowSize);
		this.libraryType = libraryType;

		readStartCount = new int[getActiveWindowSize()];
		readEndCount = new int[getActiveWindowSize()];
	}

	@Override
	public void addRecordWrapperPosition(int readPosition, SAMRecordWrapper recordWrapper) {
		// nothing to be done here
	}
	
	@Override
	public void addRecordWrapperRegion(int readPosition, int length, SAMRecordWrapper recordWrapper) {
		// nothing to be done here
	}
	
	@Override
	public void addRecordWrapper(final SAMRecordWrapper recordWrapper) {
		final SAMRecord record = recordWrapper.getSAMRecord();
		
		int windowPosition1 = Coordinate.makeRelativePosition(
				getActiveWindowCoordinate(), record.getAlignmentStart());
		if (windowPosition1 != -1) {
			readStartCount[windowPosition1]++;
		}
		
		int windowPosition2 = Coordinate.makeRelativePosition(
				getActiveWindowCoordinate(), record.getAlignmentEnd());
		if (windowPosition2 != -1) {
			readStartCount[windowPosition2]++;
		}
	}
	
	@Override
	public void addData(final T data, final Coordinate coordinate) {
		final int windowPosition = Coordinate.makeRelativePosition(getActiveWindowCoordinate(), coordinate.getPosition());
		data.getReadInfoCount().setStart(readStartCount[windowPosition]);
		data.getReadInfoCount().setEnd(readEndCount[windowPosition]);

		final int inner = data.getBaseCallCount().getCoverage() - (data.getReadInfoCount().getStart() + data.getReadInfoCount().getEnd());
		data.getReadInfoCount().setInner(inner);

		int arrest = 0;
		int through = 0;

		switch (libraryType) {

		case UNSTRANDED:
			arrest 	+= data.getReadInfoCount().getStart();
			arrest 	+= data.getReadInfoCount().getEnd();
			through += data.getReadInfoCount().getInner();
			break;

		case FR_FIRSTSTRAND:
			arrest 	+= data.getReadInfoCount().getEnd();
			through += data.getReadInfoCount().getInner();
			break;

		case FR_SECONDSTRAND:
			arrest 	+= data.getReadInfoCount().getStart();
			through += data.getReadInfoCount().getInner();
			break;
			
		case MIXED:
			throw new IllegalArgumentException("Cannot determine read arrest and read through from library type: " + libraryType.toString());
		}

		data.getReadInfoCount().setArrest(arrest);
		data.getReadInfoCount().setThrough(through);
		
	}
	
	@Override
	public void clear() {
		Arrays.fill(readStartCount, 0);
		Arrays.fill(readEndCount, 0);
	}

	public int getReadStartCount(final Coordinate coordinate) {
		final int windowPosition = Coordinate.makeRelativePosition(getActiveWindowCoordinate(), coordinate.getPosition());
		return readStartCount[windowPosition];
	}

	public int getReadEndCount(final Coordinate coordinate) {
		final int windowPosition = Coordinate.makeRelativePosition(getActiveWindowCoordinate(), coordinate.getPosition());
		return readEndCount[windowPosition];
	}
	
}
