package lib.data.cache.container;

import htsjdk.samtools.SAMRecord;
import lib.data.builder.recordwrapper.SAMRecordWrapper;

public class RFPairedEnd1CacheContainer 
extends AbstractStrandedCacheContainer {

	public RFPairedEnd1CacheContainer(final CacheContainer forwardContainer, 
			final CacheContainer reverseContainer) {

		super(forwardContainer, reverseContainer);
	}
	
	@Override
	protected CacheContainer getCacheContainer(final SAMRecordWrapper recordWrapper) {
		final SAMRecord record = recordWrapper.getSAMRecord();
	
		// paired end
		if (record.getReadPairedFlag()) { 
			if (record.getFirstOfPairFlag() && record.getReadNegativeStrandFlag() || 
					record.getSecondOfPairFlag() && ! record.getReadNegativeStrandFlag()) {
				return getForwardContainer();
			}
			return getReverseContainer();
		} 

		// single end
		if (record.getReadNegativeStrandFlag()) {
			return getForwardContainer();
		} 
		return getReverseContainer();
	}
	
}
