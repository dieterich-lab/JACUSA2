package jacusa.filter.cache.processrecord;

import lib.data.builder.recordwrapper.SAMRecordWrapper;

public interface ProcessRecord {

	public abstract void processRecord(final SAMRecordWrapper recordWrapper);
	
}