package lib.data.cache.readsubstitution;

import lib.util.Base;
import java.util.SortedSet;

import htsjdk.samtools.SAMRecord;
import lib.cli.options.has.HasReadSubstitution.BaseSubstitution;
import lib.data.adder.IncrementAdder;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.cache.container.SharedCache;
import lib.data.cache.region.isvalid.BaseCallValidator;

public class StrandedReadSubstitutionCache
extends AbstractReadSubstitutionCache {

	public StrandedReadSubstitutionCache(
			final SharedCache sharedCache,
			final BaseCallValidator validator,
			final SortedSet<BaseSubstitution> baseSubstitutions, 
			final IncrementAdder[] substBccAdders) {

		super(sharedCache, validator, baseSubstitutions, substBccAdders);
	}

	@Override
	protected Base getReadBase(SAMRecordWrapper recordWrapper, int readMismatchPos) {
		final SAMRecord record = recordWrapper.getSAMRecord();
		final Base base = Base.valueOf(record.getReadBases()[readMismatchPos]);
		if (record.getReadNegativeStrandFlag()) {
			return base.getComplement();
		}
		return base;
	}
	
	@Override
	protected Base getRefBase(SAMRecordWrapper recordWrapper, int refPos) {
		final SAMRecord record = recordWrapper.getSAMRecord();
		final Base base = recordWrapper.getRecordReferenceProvider().getReferenceBase(refPos);
		if (record.getReadNegativeStrandFlag()) {
			return base.getComplement();
		}
		return base;
	}
		
}
