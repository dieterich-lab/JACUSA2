package lib.record;

import java.util.Arrays;

import htsjdk.samtools.SAMRecordIterator;
import lib.cli.parameter.ConditionParameter;

/**
 * TODO
 */
public class EnforceBASQRecordIterator implements RecordIterator {

	private final RecordIterator recordIt;
	private final byte enforcedBASQ;
	
	public EnforceBASQRecordIterator(
			final ConditionParameter conditionParameter,
			final String fileName,
			final byte enforcedBASQ,
			final SAMRecordIterator iterator) {

		recordIt 			= new DefaultRecordIterator(conditionParameter, fileName, iterator);
		this.enforcedBASQ 	= enforcedBASQ;
	}

	public void updateIterator(SAMRecordIterator iterator) {
		recordIt.updateIterator(iterator);
	}
	
	@Override
	public boolean hasNext() {
		return recordIt.hasNext();
	}
	
	@Override
	public Record next() {
		Record record = recordIt.next();
		if (record != null) {
			final int n = record.getSAMRecord().getBaseQualityString().length();
			if (n > 0) {
				final byte[] newQuals = new byte[n];
				Arrays.fill(newQuals, enforcedBASQ);
				record.getSAMRecord().setBaseQualities(newQuals);
			}
		}
		
		return record;
	}
	
	public void close() {
		recordIt.close();
	}
	
}
