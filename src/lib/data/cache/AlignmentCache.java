package lib.data.cache;

import java.util.Arrays;

import lib.method.AbstractMethodFactory;
import lib.util.Coordinate;

import htsjdk.samtools.SAMRecord;

import lib.data.AbstractData;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.has.hasReadInfoCount;

// TODO do we consider read end by quality or by alignment
public class AlignmentCache<T extends AbstractData & hasReadInfoCount> 
extends AbstractCache<T> {

	private final int[] readStartCount;
	private final int[] readEndCount;

	public AlignmentCache(final AbstractMethodFactory<T> methodFactory) {
		super(methodFactory);

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
	public T getData(final Coordinate coordinate) {
		final T data = getDataGenerator().createData();

		final int windowPosition = Coordinate.makeRelativePosition(getActiveWindowCoordinate(), coordinate.getPosition());
		data.getReadInfoCount().setStart(readStartCount[windowPosition]);
		data.getReadInfoCount().setEnd(readEndCount[windowPosition]);

		return data;

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
