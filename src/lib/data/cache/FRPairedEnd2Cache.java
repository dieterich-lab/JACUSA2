package lib.data.cache;

import htsjdk.samtools.SAMRecord;
import lib.data.AbstractData;
import lib.data.builder.recordwrapper.SAMRecordWrapper;

public class FRPairedEnd2Cache<T extends AbstractData> 
extends AbstractStrandedCache<T> {

	public FRPairedEnd2Cache(final Cache<T> forward, final Cache<T> reverse) {
		super(forward, reverse);
	}
	
	@Override
	protected Cache<T> getCache(SAMRecordWrapper recordWrapper) {
		final SAMRecord record = recordWrapper.getSAMRecord();
		
		// paired end
		if (record.getReadPairedFlag()) { 
			if (record.getFirstOfPairFlag() && record.getReadNegativeStrandFlag() || 
					record.getSecondOfPairFlag() && ! record.getReadNegativeStrandFlag() ) {
				return getReverse();
			}
			return getForward();
		} 
		
		// single end
		if (record.getReadNegativeStrandFlag()) {
			return getReverse();
		}
		return getForward();
	}

}
