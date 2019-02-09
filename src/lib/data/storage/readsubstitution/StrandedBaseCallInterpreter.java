package lib.data.storage.readsubstitution;

import lib.util.Base;
import lib.recordextended.SAMRecordExtended;
import htsjdk.samtools.SAMRecord;

public class StrandedBaseCallInterpreter
implements BaseCallInterpreter {

	@Override
	public Base getReadBase(SAMRecordExtended recordExtended, int readPos) {
		final SAMRecord record = recordExtended.getSAMRecord();
		final Base base = Base.valueOf(record.getReadBases()[readPos]);
		if (record.getReadNegativeStrandFlag()) {
			return base.getComplement();
		}
		return base;
	}
	
	@Override
	public Base getRefBase(SAMRecordExtended recordExtended, int refPos) {
		final SAMRecord record = recordExtended.getSAMRecord();
		final Base base = recordExtended.getRecordReferenceProvider().getReferenceBase(refPos);
		if (record.getReadNegativeStrandFlag()) {
			return base.getComplement();
		}
		return base;
	}
		
}
