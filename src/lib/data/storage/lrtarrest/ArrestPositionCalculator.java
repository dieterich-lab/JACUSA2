package lib.data.storage.lrtarrest;


import htsjdk.samtools.SAMRecord;

public interface ArrestPositionCalculator {

	int get(SAMRecord record);
	
}
