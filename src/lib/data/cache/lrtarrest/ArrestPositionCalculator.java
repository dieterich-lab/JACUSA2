package lib.data.cache.lrtarrest;


import htsjdk.samtools.SAMRecord;

public interface ArrestPositionCalculator {

	int get(SAMRecord record);
	
}
