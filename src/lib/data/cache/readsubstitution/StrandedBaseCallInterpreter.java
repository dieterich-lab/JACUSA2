package lib.data.cache.readsubstitution;

import lib.util.Base;

import htsjdk.samtools.SAMRecord;
import lib.data.builder.recordwrapper.SAMRecordWrapper;

public class StrandedBaseCallInterpreter
implements BaseCallInterpreter {

	protected StrandedBaseCallInterpreter() {}
	
	@Override
	public Base getReadBase(SAMRecordWrapper recordWrapper, int readPosition) {
		final SAMRecord record = recordWrapper.getSAMRecord();
		final Base base = Base.valueOf(record.getReadBases()[readPosition]);
		if (record.getReadNegativeStrandFlag()) {
			return base.getComplement();
		}
		return base;
	}
	
	@Override
	public Base getRefBase(SAMRecordWrapper recordWrapper, int referencePosition) {
		final SAMRecord record = recordWrapper.getSAMRecord();
		final Base base = recordWrapper.getRecordReferenceProvider().getReferenceBase(referencePosition);
		if (record.getReadNegativeStrandFlag()) {
			return base.getComplement();
		}
		return base;
	}
		
}
