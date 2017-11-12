package lib.data.builder;

import java.util.Arrays;
import java.util.Iterator;

import lib.util.Coordinate;

import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMRecordIterator;

public class SAMRecordWrapperIterator implements Iterator<SAMRecordWrapper> {

	private SAMRecordWrapperIteratorProvider provider;
	private Coordinate activeWindowCoordinate;
	private SAMRecordIterator iterator;
	
	private int bufferSize;
	private int bufferPosition;
	private final SAMRecordWrapper[] buffer;
	
	public SAMRecordWrapperIterator(final SAMRecordWrapperIteratorProvider provider, 
			final Coordinate activeWindowCoordinate,
			final SAMRecordIterator iterator) {
		this.provider = provider;
		this.activeWindowCoordinate = activeWindowCoordinate;
		this.iterator	= iterator;

		bufferSize		= 0;
		bufferPosition	= 0;
		buffer			= new SAMRecordWrapper[40000];
	}

	@Override
	public boolean hasNext() {
		if (bufferSize == 0 || bufferPosition >= bufferSize) { // if buffer empty or exhausted
			if (iterator == null) {
				return false;
			}	
			
			// try to fill it
			try {
				bufferSize = processIterator(activeWindowCoordinate);
			}
			catch  (Exception e) {
				e.printStackTrace();
			}
			
			return bufferSize > 0;
		}

		return true;
	}
	
	@Override
	public SAMRecordWrapper next() {
		if (hasNext()) {
			return buffer[bufferPosition++];
		}

		return null;
	}
	
	public SAMRecordWrapper getNext() {
		if (hasNext()) {
			return buffer[bufferPosition];
		}

		return null;
	}

	public void updateActiveWindowCoordinate(final Coordinate activeWindowCoordinate) {
		this.activeWindowCoordinate = activeWindowCoordinate;
		reset();
	}

	public Coordinate getActiveWindowCoordinate() {
		return activeWindowCoordinate;
	}
	
	// clear buffer
	private void reset() {
		bufferSize = 0;
		bufferPosition = 0;
		Arrays.fill(buffer, null);
	}

	private int processIterator(final Coordinate windowCoordinates) {
		while (iterator.hasNext() && bufferSize < buffer.length) {
			final SAMRecord record = iterator.next();
			// ignore left overlapping records they will be taken from left thread 
			if (record.getAlignmentStart() < windowCoordinates.getStart()) {
				continue;
			}

			boolean isValid = false; 
			if(provider.getConditionParameter().isValid(record)) {
				isValid = true;
				provider.incrementAcceptedSAMRecords();
			} else {
				provider.incrementFilteredSAMRecords();
			}

			// TODO overlapping windows
			final SAMRecordWrapper recordWrapper = new SAMRecordWrapper(isValid, record);
			buffer[bufferSize++] = recordWrapper;
		}
		
		if (! iterator.hasNext()) {
			iterator.close();
			iterator = null;
		}

		return bufferSize;
	}
	
	public void close() {
		if (iterator != null) {
			iterator.close();
			iterator = null;
		}
	}

}
