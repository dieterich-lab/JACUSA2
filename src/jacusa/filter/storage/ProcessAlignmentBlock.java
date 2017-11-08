package jacusa.filter.storage;

import htsjdk.samtools.CigarElement;
import htsjdk.samtools.SAMRecord;

public interface ProcessAlignmentBlock {

	public abstract void process(int windowPosition, int readPosition,
			int genomicPosition, CigarElement cigarElement, SAMRecord record);
	
	public char getC();

}