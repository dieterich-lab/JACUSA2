package lib.data.builder.recordwrapper;

import java.util.Arrays;
import java.util.Iterator;

import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMRecordIterator;

public class SAMRecordWrapperIterator implements Iterator<SAMRecordWrapper> {

	private SAMRecordWrapperIteratorProvider provider;

	private int bufferSize;
	private int bufferPosition;
	private final SAMRecordWrapper[] buffer;
	private SAMRecordIterator iterator; 
	
	public SAMRecordWrapperIterator(final SAMRecordWrapperIteratorProvider provider, 
			final SAMRecordIterator iterator) {
		this.provider = provider;

		bufferSize		= 0;
		bufferPosition	= 0;
		buffer			= new SAMRecordWrapper[40000];
		this.iterator   = iterator;
	}

	@Override
	public boolean hasNext() {
		if (bufferSize == 0 || bufferPosition >= bufferSize) { // if buffer empty or exhausted
			if (iterator == null) {
				return false;
			}
			reset();

			// try to fill it
			try {
				bufferSize = processIterator();
				bufferPosition = 0;
			}
			catch  (Exception e) {
				e.printStackTrace();
			}
			
			return bufferSize > 0;
		}

		return buffer[bufferPosition] != null;
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
	
	// clear buffer
	private void reset() {
		bufferSize = 0;
		bufferPosition = 0;
		Arrays.fill(buffer, null);
	}
	
	private int processIterator() {
		while (iterator.hasNext() && bufferSize < buffer.length) {
			final SAMRecord record = iterator.next();

			boolean isValid = false; 
			if(provider.getConditionParameter().isValid(record)) {
				isValid = true;
				provider.incrementAcceptedSAMRecords();
			} else {
				provider.incrementFilteredSAMRecords();
			}

			if (isValid) {
				final SAMRecordWrapper recordWrapper = new SAMRecordWrapper(isValid, record);
				buffer[bufferSize++] = recordWrapper;
			}
		}
		
		if (! iterator.hasNext()) {
			close();
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
