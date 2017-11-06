package jacusa.filter.storage;

import net.sf.samtools.CigarElement;
import net.sf.samtools.SAMRecord;

public interface ProcessSkippedOperator {

	public abstract void processSkippedOperator(int windowPosition, int readPosition,
			int genomicPosition, int upstreamMatch, int downstreamMatch,
			CigarElement cigarElement, SAMRecord record);
	
	public abstract char getC();

}