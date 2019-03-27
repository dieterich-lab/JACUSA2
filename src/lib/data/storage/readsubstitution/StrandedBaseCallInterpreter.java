package lib.data.storage.readsubstitution;

import lib.util.Base;
import lib.util.position.Position;
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
	public Base getRefBase(SAMRecordExtended recordExtended, Position pos) {
		final SAMRecord record = recordExtended.getSAMRecord();
		final Base base = recordExtended
				.getRecordReferenceProvider()
				.getReferenceBase(pos.getReferencePosition(), pos.getReadPosition());
		if (record.getReadNegativeStrandFlag()) {
			return base.getComplement();
		}
		return base;
	}
		
}
