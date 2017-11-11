package lib.data.cache;

import java.util.ArrayList;
import java.util.List;

import lib.method.AbstractMethodFactory;
import lib.util.Coordinate;

import htsjdk.samtools.AlignmentBlock;
import htsjdk.samtools.SAMRecord;

import lib.data.AbstractData;
import lib.data.builder.SAMRecordWrapper;
import lib.data.has.hasRecordWrapper;

public class SAMRecordCache<T extends AbstractData & hasRecordWrapper> 
extends AbstractCache<T> {

	private List<List<SAMRecordWrapper>> recordWrappers;

	public SAMRecordCache(final AbstractMethodFactory<T> methodFactory) {
		super(methodFactory);

		recordWrappers = new ArrayList<List<SAMRecordWrapper>>(getActiveWindowSize());
		for (int i = 0; i < getActiveWindowSize(); ++i) {
			recordWrappers.add(new ArrayList<SAMRecordWrapper>(50));
		}
	}

	@Override
	public void addRecordWrapperPosition(final int readPosition, final SAMRecordWrapper recordWrapper) {
		final SAMRecord record = recordWrapper.getSAMRecord();
		final int referencePosition = record.getReferencePositionAtReadPosition(readPosition);
		final int windowPosition = Coordinate.makeRelativePosition(getActiveWindowCoordinates(), referencePosition);
		recordWrappers.get(windowPosition).add(recordWrapper);
	}

	// TODO
	public void addRecordWrapper(final SAMRecordWrapper recordWrapper) {
		final SAMRecord record = recordWrapper.getSAMRecord();

		for (final AlignmentBlock block : record.getAlignmentBlocks()) {
			int referencePosition = block.getReferenceStart();
			int windowPosition = Coordinate.makeRelativePosition(getActiveWindowCoordinates(), referencePosition);
			int readPosition = block.getReadStart() - 1;

			// alignment length
			int length = block.getLength();
			
			if (windowPosition == -1) {
				windowPosition = referencePosition - getActiveWindowCoordinates().getStart();
				if (windowPosition > getActiveWindowSize()) { // downtstream of window -> ignore TODO distance
					continue;
				}
				// alignment outside of window - upstream TODO distance
				if (windowPosition + length < 0) { 
					continue;
				}
				final int offset = Math.abs(windowPosition); 
				windowPosition += offset;
				readPosition += offset;
				length -= offset;
			}

			int lengthOffset = getActiveWindowSize() - (windowPosition + length);
			if (lengthOffset <= 0) {
				incrementBaseCalls(windowPosition, readPosition, length + lengthOffset, recordWrapper);
				return;
			}
			incrementBaseCalls(windowPosition, readPosition, length, recordWrapper);
		}
	}
		
	@Override
	public T getData(final Coordinate coordinate) {
		final T data = getMethodFactory().createData();
		final int windowPosition = getWindowPosition(coordinate); 
		data.getRecordWrapper().addAll(recordWrappers.get(windowPosition));
		return data;
	}
	
	protected void incrementBaseCalls(final int windowPosition, final int readPosition, final int length, 
			final SAMRecordWrapper recordWrapper) {

		for (int i = 0; i < length; ++i) {
			recordWrappers.get(windowPosition + i).add(recordWrapper);
		}
	}

	@Override
	public void clear() {
		for (final List<SAMRecordWrapper> r : recordWrappers) {
			r.clear();
		}
	}

	public List<SAMRecordWrapper> getRecordWrapper(final int windowPosition) {
		return recordWrappers.get(windowPosition);
	}

	@Override
	public void addRecordWrapperRegion(int readPosition, int length, SAMRecordWrapper recordWrapper) {
		final SAMRecord record = recordWrapper.getSAMRecord();
		final int referencePosition = record.getReferencePositionAtReadPosition(readPosition);
		final int windowPosition = Coordinate.makeRelativePosition(getActiveWindowCoordinates(), referencePosition);
		incrementBaseCalls(windowPosition, readPosition, length, recordWrapper);
	}

}
