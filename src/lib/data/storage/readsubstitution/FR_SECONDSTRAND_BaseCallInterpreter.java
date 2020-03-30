package lib.data.storage.readsubstitution;

import lib.record.Record;
import lib.util.Base;
import lib.util.position.Position;
import htsjdk.samtools.SAMRecord;

public class FR_SECONDSTRAND_BaseCallInterpreter
implements BaseCallInterpreter {

	@Override
	public Base getReadBase(Record record, int readPos) {
		final SAMRecord samRecord = record.getSAMRecord();
		final Base base = Base.valueOf(samRecord.getReadBases()[readPos]);
		return getBase(samRecord, base);
	}

	@Override
	public Base getRefBase(Record record, Position pos) {
		final SAMRecord samRecord = record.getSAMRecord();
		final Base base = record
				.getRecordReferenceProvider()
				.getRefBase(pos.getReferencePosition(), pos.getReadPosition());
		return getBase(samRecord, base);
	}

	private Base getBase(final SAMRecord samRecord, final Base base) {
		if (samRecord.getReadPairedFlag()) { 
			if (samRecord.getFirstOfPairFlag() && samRecord.getReadNegativeStrandFlag() || 
					samRecord.getSecondOfPairFlag() && ! samRecord.getReadNegativeStrandFlag() ) {
				return base.getComplement();
			}
			return base;
		} 
		
		// single end
		if (samRecord.getReadNegativeStrandFlag()) {
			return base.getComplement();
		}
		return base;
	}
}
