package lib.data.cache;

import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.cache.container.SharedCache;
import lib.data.has.LibraryType;

public class FR_SECONDSTRAND_RTarrestDataCache 
extends AbstractRTarrestDataCache {

	public FR_SECONDSTRAND_RTarrestDataCache(final byte minBASQ, final SharedCache sharedCache) {
		super(LibraryType.FR_SECONDSTRAND, minBASQ, sharedCache);
	}
	
	@Override
	public void processRecordWrapper(SAMRecordWrapper recordWrapper) {
		final int size = recordWrapper.getSAMRecord().getAlignmentBlocks().size();
		
		addFirstAlignmentBlockToArrest(recordWrapper);
		addFirstAlignmentBlockToThrough(recordWrapper);
		addAlignmentBlockToThrough(1, size, recordWrapper);
	}

}
