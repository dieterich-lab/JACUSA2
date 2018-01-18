package lib.data.cache;

import java.util.Arrays;

import lib.tmp.CoordinateController;
import lib.util.coordinate.Coordinate;

import htsjdk.samtools.SAMRecord;

import lib.data.AbstractData;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.has.hasBaseCallCount;
import lib.data.has.hasReadInfoCount;
import lib.data.has.hasLibraryType.LIBRARY_TYPE;

// TODO do we consider read end by quality or by alignment
public class AlignmentDataCache<T extends AbstractData & hasBaseCallCount & hasReadInfoCount> 
extends AbstractDataCache<T> {

	private final LIBRARY_TYPE libraryType;

	private final int[] readStartCount;
	private final int[] readEndCount;

	public AlignmentDataCache(final LIBRARY_TYPE libraryType, final CoordinateController coordinateController) {
		super(coordinateController);
		this.libraryType = libraryType;

		readStartCount = new int[coordinateController.getActiveWindowSize()];
		readEndCount = new int[coordinateController.getActiveWindowSize()];
	}

	@Override
	public void addRecordWrapper(final SAMRecordWrapper recordWrapper) {
		final SAMRecord record = recordWrapper.getSAMRecord();
		
		int windowPosition1 = getCoordinateController().convert2windowPosition(record.getAlignmentStart());
		if (windowPosition1 >= 0) {
			readStartCount[windowPosition1]++;
		}
		
		int windowPosition2 = getCoordinateController().convert2windowPosition(record.getAlignmentEnd());
		if (windowPosition2 >= 0) {
			readEndCount[windowPosition2]++;
		}
		
	}
	
	@Override
	public void addData(final T data, final Coordinate coordinate) {
		final int windowPosition = getCoordinateController().convert2windowPosition(coordinate);
		if (data.getBaseCallCount().getCoverage() == 0) {
			return;
		}
		
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
		final int windowPosition = getCoordinateController().convert2windowPosition(coordinate);
		return readStartCount[windowPosition];
	}

	public int getReadEndCount(final Coordinate coordinate) {
		final int windowPosition = getCoordinateController().convert2windowPosition(coordinate);
		return readEndCount[windowPosition];
	}
	
}
