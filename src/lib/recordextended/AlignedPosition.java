package lib.recordextended;

import htsjdk.samtools.AlignmentBlock;
import htsjdk.samtools.CigarElement;
import htsjdk.samtools.CigarOperator;
import htsjdk.samtools.SAMRecord;
import lib.util.Copyable;

public class AlignedPosition implements Copyable<AlignedPosition>{
	
	private int readPos;
	private int refPos;
	private int matches;
	
	public AlignedPosition(final int refPos) {
		this(0, refPos, 0);
	}
	
	public AlignedPosition(final SAMRecord record) {
		this(record.getAlignmentBlocks().get(0));
	}
	
	private AlignedPosition(final AlignmentBlock block) {
		this(block.getReadStart() - 1, block.getReferenceStart(), 0);
	}
	
	private AlignedPosition(final int readPos, final int refPos, final int matches) {
		this.readPos 	= readPos;
		this.refPos 	= refPos;
		this.matches 	= matches;
	}

	public void advance(final CigarElement e) {
		if (e.getOperator().consumesReferenceBases()) {
			refPos += e.getLength();
			if (e.getOperator() != CigarOperator.N) { 
				matches += e.getLength();
			}
		}
		
		if (e.getOperator().consumesReadBases()) {
			readPos += e.getLength();
		}
	}
	
	public AlignedPosition advance(final int offset) {
		refPos 	+= offset;
		matches += offset;
		readPos += offset;
		return this;
	}
	
	public int getReadPosition() {
		return readPos;
	}
	
	public int getReferencePosition() {
		return refPos;
	}

	public int getMatches() {
		return matches;
	}
	
	@Override
	public AlignedPosition copy() {
		return new AlignedPosition(readPos, refPos, matches);
	}
	
}
