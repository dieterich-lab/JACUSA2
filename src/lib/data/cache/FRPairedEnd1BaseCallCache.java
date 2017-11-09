package lib.data.cache;

import htsjdk.samtools.SAMRecord;
import lib.cli.options.BaseCallConfig;
import lib.data.builder.SAMRecordWrapper;

public class FRPairedEnd1BaseCallCache extends AbstractStrandedBaseCallCache {

	public FRPairedEnd1BaseCallCache(final BaseCallConfig baseCallConfig, final int activeWindowSize) {
		super(baseCallConfig, activeWindowSize);
	}
	
	@Override
	public void addRecordWrapper(final SAMRecordWrapper recordWrapper) {
		final SAMRecord record = recordWrapper.getSAMRecord();
		final BaseCallCache tmp;
		
		if (record.getReadPairedFlag()) { // paired end
			if (record.getFirstOfPairFlag() && record.getReadNegativeStrandFlag() || 
					record.getSecondOfPairFlag() && ! record.getReadNegativeStrandFlag()) {
				tmp = getForward();
			} else {
				tmp = getReverse();
			}
		} else { // single end
			if (record.getReadNegativeStrandFlag()) {
				tmp = getForward();
			} else {
				tmp = getReverse();
			}
		}

		tmp.addRecordWrapper(recordWrapper);
	}

}
