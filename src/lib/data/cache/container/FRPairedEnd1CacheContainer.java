package lib.data.cache.container;

import htsjdk.samtools.SAMRecord;
import lib.data.AbstractData;
import lib.data.builder.recordwrapper.SAMRecordWrapper;

public class FRPairedEnd1CacheContainer<T extends AbstractData> 
extends AbstractStrandedCacheContainer<T> {

	public FRPairedEnd1CacheContainer(final CacheContainer<T> forwardContainer, 
			final CacheContainer<T> reverseContainer) {

		super(forwardContainer, reverseContainer);
	}
	
	@Override
	protected CacheContainer<T> getCacheContainer(final SAMRecordWrapper recordWrapper) {
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
