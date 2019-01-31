package lib.data.storage.container;

import htsjdk.samtools.SAMRecord;
import lib.recordextended.SAMRecordExtended;

public class FRPairedEnd2CacheContainer 
extends AbstractStrandedCacheContainer {

	public FRPairedEnd2CacheContainer(final CacheContainer forwardContainer, 
			final CacheContainer reverseContainer) {
		
		super(forwardContainer, reverseContainer);
	}
	
	@Override
	protected CacheContainer getCacheContainer(SAMRecordExtended recordExtended) {
		final SAMRecord record = recordExtended.getSAMRecord();
		
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
