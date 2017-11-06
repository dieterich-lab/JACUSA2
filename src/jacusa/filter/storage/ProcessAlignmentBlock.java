package jacusa.filter.storage;

import net.sf.samtools.CigarElement;
import net.sf.samtools.SAMRecord;

public interface ProcessAlignmentBlock {

	public abstract void process(int windowPosition, int readPosition,
			int genomicPosition, CigarElement cigarElement, SAMRecord record);
	
	public char getC();

}