package lib.data.storage.container;

import htsjdk.samtools.SAMRecord;
import lib.recordextended.SAMRecordExtended;

public class RFPairedEnd1CacheContainer 
extends AbstractStrandedCacheContainer {

	public RFPairedEnd1CacheContainer(final CacheContainer forwardContainer, 
			final CacheContainer reverseContainer) {

		super(forwardContainer, reverseContainer);
	}
	
	@Override
	protected CacheContainer getCacheContainer(final SAMRecordExtended recordExtended) {
		final SAMRecord record = recordExtended.getSAMRecord();
	
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
