package lib.data.cache;

import htsjdk.samtools.SAMRecord;
import lib.cli.options.BaseCallConfig;
import lib.data.builder.SAMRecordWrapper;

public class FRPairedEnd2BaseCallCache extends AbstractStrandedBaseCallCache {

	public FRPairedEnd2BaseCallCache(final BaseCallConfig baseCallConfig, final int activeWindowSize) {
		super(baseCallConfig, activeWindowSize);
	}
	
	@Override
	public void addRecordWrapper(final SAMRecordWrapper recordWrapper) {
		final SAMRecord record = recordWrapper.getSAMRecord();
		final BaseCallCache tmp;
		
		if (record.getReadPairedFlag()) { // paired end
			if (record.getFirstOfPairFlag() && record.getReadNegativeStrandFlag() || 
					record.getSecondOfPairFlag() && ! record.getReadNegativeStrandFlag() ) {
				tmp = getReverse();
			} else {
				tmp = getForward();
			}
		} else { // single end
			if (record.getReadNegativeStrandFlag()) {
				tmp = getReverse();
			} else {
				tmp = getForward();
			}
		}

		tmp.addRecordWrapper(recordWrapper);
	}

}
