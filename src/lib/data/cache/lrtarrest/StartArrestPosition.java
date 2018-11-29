package lib.data.cache.lrtarrest;


import htsjdk.samtools.SAMRecord;

public class StartArrestPosition
implements ArrestPositionCalculator {

	@Override
	public int get(SAMRecord record) {
		return record.getAlignmentStart();
	}
	
}
