package jacusa.filter.storage;

import lib.data.builder.recordwrapper.SAMRecordWrapper;

public interface ProcessRecord {

	// TODO remove public abstract void processRecord(int genomicWindowStart, SAMRecordWrapper recordWrapper);
	public abstract void processRecord(final SAMRecordWrapper recordWrapper);
	public abstract char getC();
	
}