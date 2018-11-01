package lib.data.cache;

import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.cache.container.SharedCache;
import lib.data.has.LibraryType;

public class UNSTRANDED_RTarrestDataCache 
extends AbstractRTarrestDataCache {

	public UNSTRANDED_RTarrestDataCache(
			final byte minBASQ, 
			final SharedCache sharedCache) {
		
		super(LibraryType.UNSTRANDED, minBASQ,sharedCache);
	}
	
	@Override
	public void processRecordWrapper(SAMRecordWrapper recordWrapper) {
		final int size = recordWrapper.getSAMRecord().getAlignmentBlocks().size();

		addFirstAlignmentBlockToArrest(recordWrapper);
		addLastAlignmentBlockToArrest(recordWrapper);
		if (size == 1) {
			addSingleAlignmentBlockToThrough(recordWrapper);
		} else {
			addFirstAlignmentBlockToThrough(recordWrapper);
			addLastAlignmentBlockToThrough(recordWrapper);
			addAlignmentBlockToThrough(1, size - 1, recordWrapper);
		}
	}

}
