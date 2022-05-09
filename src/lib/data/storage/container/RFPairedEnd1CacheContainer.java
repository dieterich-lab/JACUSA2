package lib.data.storage.container;

import htsjdk.samtools.SAMRecord;
import lib.record.ProcessedRecord;

public class RFPairedEnd1CacheContainer 
extends AbstractStrandedCacheContainer {

	public RFPairedEnd1CacheContainer(final CacheContainer forwardContainer, 
			final CacheContainer reverseContainer) {

		super(forwardContainer, reverseContainer);
	}
	
	@Override
	protected CacheContainer getCacheContainer(final ProcessedRecord record) {
		final SAMRecord samRecord = record.getSAMRecord();
	
		// paired end
		if (samRecord.getReadPairedFlag()) { 
			if (samRecord.getFirstOfPairFlag() && samRecord.getReadNegativeStrandFlag() || 
					samRecord.getSecondOfPairFlag() && ! samRecord.getReadNegativeStrandFlag()) {
				return getForwardContainer();
			}
			return getReverseContainer();
		} 

		// single end
		if (samRecord.getReadNegativeStrandFlag()) {
			return getForwardContainer();
		} 
		return getReverseContainer();
	}
	
}
