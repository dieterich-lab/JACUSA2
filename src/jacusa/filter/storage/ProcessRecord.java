package jacusa.filter.storage;

import htsjdk.samtools.SAMRecord;

public interface ProcessRecord {

	public abstract void processRecord(int genomicWindowStart, SAMRecord record);
	public abstract char getC();
	
}