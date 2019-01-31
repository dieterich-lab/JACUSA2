package lib.data.storage.readsubstitution;

import lib.util.Base;
import lib.recordextended.SAMRecordExtended;

public class UnstrandedBaseCallInterpreter
implements BaseCallInterpreter {

	protected UnstrandedBaseCallInterpreter() {}
	
	@Override
	public Base getReadBase(SAMRecordExtended recordExtended, int readPos) {
		return Base.valueOf(recordExtended.getSAMRecord().getReadBases()[readPos]);
	}
	
	@Override
	public Base getRefBase(SAMRecordExtended recordExtended, int refPos) {
		return recordExtended.getRecordReferenceProvider().getReferenceBase(refPos);
	}
	
}
