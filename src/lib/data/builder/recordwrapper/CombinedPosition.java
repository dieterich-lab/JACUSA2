package lib.data.builder.recordwrapper;

import htsjdk.samtools.CigarElement;
import lib.util.Copyable;

public class CombinedPosition implements Copyable<CombinedPosition>{
	
	private int readPos;
	private int refPos;
	
	public CombinedPosition(final int readPos, final int refPos) {
		this.readPos = readPos;
		this.refPos = refPos;
	}

	public void advance(final CigarElement e) {
		if (e.getOperator().consumesReferenceBases()) {
			refPos += e.getLength();
		}
		
		if (e.getOperator().consumesReadBases()) {
			readPos += e.getLength();
		}
	}
	
	public int getReadPosition() {
		return readPos;
	}
	
	public int getReferencePosition() {
		return refPos;
	}

	@Override
	public CombinedPosition copy() {
		return new CombinedPosition(readPos, refPos);
	}
	
}