package jacusa.filter.storage;

import htsjdk.samtools.CigarElement;
import htsjdk.samtools.SAMRecord;

public interface ProcessAlignmentOperator {

	public abstract void processAlignmentOperator(int windowPosition, int readPosition,
			int genomicPosition, CigarElement cigarElement, SAMRecord record,
			int baseI, int qual);
	
	public abstract char getC();

}