package lib.data.storage.readsubstitution;

import lib.record.ProcessedRecord;
import lib.util.Base;
import lib.util.position.Position;

public class UnstrandedBaseCallInterpreter
implements BaseCallInterpreter {

	protected UnstrandedBaseCallInterpreter() {}
	
	@Override
	public Base getReadBase(ProcessedRecord record, int readPos) {
		return Base.valueOf(record.getSAMRecord().getReadBases()[readPos]);
	}
	
	@Override
	public Base getRefBase(ProcessedRecord record, Position pos) {
		return record
				.getRecordReferenceProvider()
				.getRefBase(pos.getReferencePosition(), pos.getReadPosition());
	}
	
}
