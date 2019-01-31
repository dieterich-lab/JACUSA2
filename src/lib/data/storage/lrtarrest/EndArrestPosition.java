package lib.data.storage.lrtarrest;


import htsjdk.samtools.SAMRecord;

public class EndArrestPosition
implements ArrestPositionCalculator {

	@Override
	public int get(SAMRecord record) {
		return record.getAlignmentEnd();
	}
	
}
