package lib.record;

import java.util.Arrays;
import java.util.Iterator;

import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMRecordIterator;
import htsjdk.samtools.SamReader;
import lib.cli.parameter.ConditionParameter;

/**
 * DOCUMENT
 */
public class RecordIterator implements Iterator<Record> {

	private final ConditionParameter conditionParameter;

	private final SamReader mateReader;
	
	private int acceptedSAMRecords;
	private int filteredSAMRecords;
	
	private int bufferSize;
	private int bufferPosition;
	private final Record[] buffer;
	
	private SAMRecordIterator iterator; 
	
	public RecordIterator(
			final ConditionParameter conditionParameter,
			final String fileName,
			final SAMRecordIterator iterator) {

		this.conditionParameter = conditionParameter;
		mateReader 				= ConditionParameter.createSamReader(fileName);
		
		acceptedSAMRecords = 0;
		filteredSAMRecords = 0;
		
		bufferSize		= 0;
		bufferPosition	= 0;
		buffer			= new Record[40000];
		
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
	public Record next() {
		if (hasNext()) {
			return buffer[bufferPosition++];
		}

		return null;
	}
	
	public Record getNext() {
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
			final SAMRecord samRecord = iterator.next();
			
			boolean isValid = false; 
			if(conditionParameter.isValid(samRecord)) {
				isValid = true;
				acceptedSAMRecords++;
			} else {
				filteredSAMRecords++;
			}
			
			if (isValid) {
				final Record record = new Record(samRecord, mateReader);
				buffer[bufferSize++] = record;
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
