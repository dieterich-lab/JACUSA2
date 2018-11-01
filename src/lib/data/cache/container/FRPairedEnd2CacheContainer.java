package lib.data.cache.container;

import htsjdk.samtools.SAMRecord;
import lib.data.builder.recordwrapper.SAMRecordWrapper;

public class FRPairedEnd2CacheContainer 
extends AbstractStrandedCacheContainer {

	public FRPairedEnd2CacheContainer(final CacheContainer forwardContainer, 
			final CacheContainer reverseContainer) {
		
		super(forwardContainer, reverseContainer);
	}
	
	@Override
	protected CacheContainer getCacheContainer(SAMRecordWrapper recordWrapper) {
		final SAMRecord record = recordWrapper.getSAMRecord();
		
		// paired end
		if (record.getReadPairedFlag()) { 
			if (record.getFirstOfPairFlag() && record.getReadNegativeStrandFlag() || 
					record.getSecondOfPairFlag() && ! record.getReadNegativeStrandFlag() ) {
				return getReverseContainer();
			}
			return getForwardContainer();
		} 
		
		// single end
		if (record.getReadNegativeStrandFlag()) {
			return getReverseContainer();
		}
		return getForwardContainer();
	}

}
