package lib.data.builder.recordwrapper;

import java.util.Arrays;
import java.util.Iterator;

import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMRecordIterator;
import lib.cli.parameter.ConditionParameter;

public class SAMRecordWrapperIterator implements Iterator<SAMRecordWrapper> {

	private final ConditionParameter conditionParameter;
	
	private int acceptedSAMRecords;
	private int filteredSAMRecords;
	
	private int bufferSize;
	private int bufferPosition;
	private final SAMRecordWrapper[] buffer;
	
	private SAMRecordIterator iterator; 
	
	public SAMRecordWrapperIterator(
			final ConditionParameter conditionParameter, 
			final SAMRecordIterator iterator) {

		this.conditionParameter = conditionParameter;
		
		acceptedSAMRecords = 0;
		filteredSAMRecords = 0;
		
		bufferSize		= 0;
		bufferPosition	= 0;
		buffer			= new SAMRecordWrapper[40000];
		
		this.iterator   = iterator;
	}

	public void updateIterator(SAMRecordIterator iterator) {
		reset();
		close();
		this.iterator = iterator;
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
			if(conditionParameter.isValid(record)) {
				isValid = true;
				acceptedSAMRecords++;
			} else {
				filteredSAMRecords++;
			}

			if (isValid) {
				final SAMRecordWrapper recordWrapper = new SAMRecordWrapper(record);
				buffer[bufferSize++] = recordWrapper;
			}
		}
		
		if (! iterator.hasNext()) {
			close();
		}

		return bufferSize;
	}

	public final int getAcceptedSAMRecords() {
		return acceptedSAMRecords;
	}
	
	public final int getFilteredSAMRecords() {
		return filteredSAMRecords;
	}
	
	public void close() {
		if (iterator != null) {
			iterator.close();
			iterator = null;
		}
	}
	
}
