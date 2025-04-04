package lib.data.storage.container;

import htsjdk.samtools.SAMRecord;
import lib.record.Record;

/**
 * DOCUMENT
 */
public class FRPairedEnd2CacheContainer 
extends AbstractStrandedCacheContainer {

	public FRPairedEnd2CacheContainer(final CacheContainer forwardContainer, 
			final CacheContainer reverseContainer) {
		
		super(forwardContainer, reverseContainer);
	}
	
	@Override
	protected CacheContainer getCacheContainer(Record record) {
		final SAMRecord samRecord = record.getSAMRecord();
		
		// paired end
		if (samRecord.getReadPairedFlag()) { 
			if (samRecord.getFirstOfPairFlag() && samRecord.getReadNegativeStrandFlag() || 
					samRecord.getSecondOfPairFlag() && ! samRecord.getReadNegativeStrandFlag() ) {
				return getReverseContainer();
			}
			return getForwardContainer();
		} 
		
		// single end
		if (samRecord.getReadNegativeStrandFlag()) {
			return getReverseContainer();
		}
		return getForwardContainer();
	}

}
