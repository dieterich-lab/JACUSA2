package lib.record;

import htsjdk.samtools.CigarElement;
import htsjdk.samtools.CigarOperator;
import lib.util.Copyable;

public class AlignedPosition implements Copyable<AlignedPosition>{
	
	private int readPos;
	private int refPos;
	private int nonSkippedMatches;
	
	public AlignedPosition(final int refPos) {
		this(0, refPos, 0);
	}
	
	public AlignedPosition(final AlignedPosition alignedPosition) {
		this(alignedPosition.readPos, alignedPosition.refPos, alignedPosition.nonSkippedMatches);
	}
	
	private AlignedPosition(final int readPos, final int refPos, final int matches) {
		this.readPos 			= readPos;
		this.refPos 			= refPos;
		this.nonSkippedMatches 	= matches;
	}

	public void advance(final CigarElement e) {
		if (e.getOperator().consumesReferenceBases()) {
			refPos += e.getLength();
			// reference bases from skipped regions aka introns are not counted in MD field 
			if (e.getOperator() != CigarOperator.N) { 
				nonSkippedMatches += e.getLength();
			}
		}
		
		if (e.getOperator().consumesReadBases()) {
			readPos += e.getLength();
		}
	}

	public AlignedPosition advance(final int offset) {
		refPos 				+= offset;
		nonSkippedMatches 	+= offset;
		readPos 			+= offset;
		return this;
	}
	
	public int getReadPosition() {
		return readPos;
	}
	
	public int getRefPosition() {
		return refPos;
	}

	public int getNonSkippedMatches() {
		return nonSkippedMatches;
	}
	
	@Override
	public AlignedPosition copy() {
		return new AlignedPosition(readPos, refPos, nonSkippedMatches);
	}
	
}
