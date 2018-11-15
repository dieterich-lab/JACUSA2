package lib.data.cache.readsubstitution;

import lib.util.Base;

import lib.data.builder.recordwrapper.SAMRecordWrapper;

public class UnstrandedBaseCallInterpreter
implements BaseCallInterpreter {

	protected UnstrandedBaseCallInterpreter() {}
	
	@Override
	public Base getReadBase(SAMRecordWrapper recordWrapper, int readPosition) {
		return Base.valueOf(recordWrapper.getSAMRecord().getReadBases()[readPosition]);
	}
	
	@Override
	public Base getRefBase(SAMRecordWrapper recordWrapper, int referencePosition) {
		return recordWrapper.getRecordReferenceProvider().getReferenceBase(referencePosition);
	}
	
}
