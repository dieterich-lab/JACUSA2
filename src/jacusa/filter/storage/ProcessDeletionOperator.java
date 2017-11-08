package jacusa.filter.storage;

import htsjdk.samtools.CigarElement;
import htsjdk.samtools.SAMRecord;

public interface ProcessDeletionOperator {

	public abstract void processDeletionOperator(int windowPosition, int readPosition,
			int genomicPosition, int upstreamMatch, int downstreamMatch,
			CigarElement cigarElement, SAMRecord record);
	
	public abstract char getC();

}