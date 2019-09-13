package lib.data.storage.readsubstitution;

import lib.record.Record;
import lib.util.Base;
import lib.util.position.Position;
import htsjdk.samtools.SAMRecord;

public class StrandedBaseCallInterpreter
implements BaseCallInterpreter {

	@Override
	public Base getReadBase(Record record, int readPos) {
		final SAMRecord samRecord = record.getSAMRecord();
		final Base base = Base.valueOf(samRecord.getReadBases()[readPos]);
		if (samRecord.getReadNegativeStrandFlag()) {
			return base.getComplement();
		}
		return base;
	}
	
	@Override
	public Base getRefBase(Record record, Position pos) {
		final SAMRecord samRecord = record.getSAMRecord();
		final Base base = record
				.getRecordReferenceProvider()
				.getRefBase(pos.getReferencePosition(), pos.getReadPosition());
		if (samRecord.getReadNegativeStrandFlag()) {
			return base.getComplement();
		}
		return base;
	}
		
}
