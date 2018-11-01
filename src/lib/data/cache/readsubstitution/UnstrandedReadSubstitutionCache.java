package lib.data.cache.readsubstitution;

import lib.util.Base;
import java.util.SortedSet;

import lib.cli.options.has.HasReadSubstitution.BaseSubstitution;
import lib.data.adder.IncrementAdder;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.cache.container.SharedCache;
import lib.data.cache.region.isvalid.BaseCallValidator;

public class UnstrandedReadSubstitutionCache
extends AbstractReadSubstitutionCache {

	public UnstrandedReadSubstitutionCache(
			final SharedCache sharedCache,
			final BaseCallValidator validator,
			final SortedSet<BaseSubstitution> baseSubstitutions, 
			final IncrementAdder[] substBccAdders) {

		super(sharedCache, validator, baseSubstitutions, substBccAdders);
	}

	@Override
	protected Base getReadBase(SAMRecordWrapper recordWrapper, int readMismatchPos) {
		return Base.valueOf(recordWrapper.getSAMRecord().getReadBases()[readMismatchPos]);
	}
	
	@Override
	protected Base getRefBase(SAMRecordWrapper recordWrapper, int refPos) {
		return recordWrapper.getRecordReferenceProvider().getReferenceBase(refPos);
	}
	
}
