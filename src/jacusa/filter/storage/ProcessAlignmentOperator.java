package jacusa.filter.storage;

import net.sf.samtools.CigarElement;
import net.sf.samtools.SAMRecord;

public interface ProcessAlignmentOperator {

	public abstract void processAlignmentOperator(int windowPosition, int readPosition,
			int genomicPosition, CigarElement cigarElement, SAMRecord record,
			int baseI, int qual);
	
	public abstract char getC();

}