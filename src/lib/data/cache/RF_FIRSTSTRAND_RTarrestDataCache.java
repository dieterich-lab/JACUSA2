package lib.data.cache;

import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.cache.container.SharedCache;
import lib.data.has.LibraryType;

public class RF_FIRSTSTRAND_RTarrestDataCache 
extends AbstractRTarrestDataCache {

	public RF_FIRSTSTRAND_RTarrestDataCache(final byte minBASQ, final SharedCache sharedCache) {
		super(LibraryType.FR_SECONDSTRAND, minBASQ, sharedCache);
	}
	
	@Override
	public void processRecordWrapper(SAMRecordWrapper recordWrapper) {
		final int size = recordWrapper.getSAMRecord().getAlignmentBlocks().size();
		
		addLastAlignmentBlockToArrest(recordWrapper);
		addLastAlignmentBlockToThrough(recordWrapper);
		addAlignmentBlockToThrough(0, size - 1, recordWrapper);
	}

}
