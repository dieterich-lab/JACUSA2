package jacusa.filter.cache.processrecord;

import lib.data.builder.recordwrapper.SAMRecordWrapper;

/**
 * Defines an interface that enables to process a read wrapped in SAMRecordWrapper.
 */
public interface ProcessRecord {

	/**
	 * Process recordWrapper.
	 * 
	 * @param recordWrapper the read to be processed
	 */
	public abstract void processRecord(SAMRecordWrapper recordWrapper);
	
}