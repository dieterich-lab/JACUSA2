package lib.data.count;

import lib.data.count.PileupCount;
import lib.data.has.HasBaseCallCount;
import lib.data.has.HasBaseCallQualityCount;
import lib.data.has.HasCoverage;
import lib.data.has.HasReferenceBase;

public class PileupCount 
implements HasBaseCallCount, HasBaseCallQualityCount, HasReferenceBase, HasCoverage {

	private byte referenceBase;
	private BaseCallCount baseCallCount;
	private BaseCallQualityCount baseQualCount;

	public PileupCount(final byte referenceBase, final BaseCallCount baseCallCount, final BaseCallQualityCount baseQualCount) {
		this.referenceBase 	= referenceBase;
		this.baseCallCount 	= baseCallCount;
		this.baseQualCount	= baseQualCount;
	}
	
	public PileupCount(final BaseCallCount baseCallCount, final BaseCallQualityCount baseQualCount) {
		this((byte)'N', baseCallCount, baseQualCount);
	}
	
	public PileupCount(final PileupCount pileupCount) {
		this(pileupCount.referenceBase, pileupCount.baseCallCount.copy(), pileupCount.baseQualCount.copy());
	}

	public PileupCount copy() {
		return new PileupCount(this);
	}

	@Override
	public BaseCallCount getBaseCallCount() {
		return baseCallCount;
	}
	
	@Override
	public BaseCallQualityCount getBaseCallQualityCount() {
		return baseQualCount;
	}

	public void add(final int baseIndex, final byte baseQual) {
		baseCallCount.increment(baseIndex);
		baseQualCount.increment(baseIndex, baseQual);
	}
		
	public void merge(final PileupCount pileupCount) {
		for (int baseIndex : pileupCount.getBaseCallCount().getAlleles()) {
			add(baseIndex, pileupCount);
		}
	}
	
	public void add(final int baseIndex, final PileupCount pileupCount) {
		add(baseIndex, baseIndex, pileupCount);
	}

	public void add(final int baseIndexDest, final int baseIndexSrc, final PileupCount pileupCount) {
		baseCallCount.add(baseIndexDest, baseIndexSrc, pileupCount.getBaseCallCount());
		baseQualCount.add(baseIndexDest, baseIndexSrc, pileupCount.getBaseCallQualityCount());
	}

	public void substract(final int baseIndex, final PileupCount pileupCount) {
		substract(baseIndex, baseIndex, pileupCount);
	}

	public void substract(final int baseIndexDest, final int baseIndexSrc, final PileupCount pileupCount) {
		baseCallCount.add(baseIndexDest, baseIndexSrc, pileupCount.getBaseCallCount());
		baseQualCount.add(baseIndexDest, baseIndexSrc, pileupCount.getBaseCallQualityCount());
	}

	public void substract(final PileupCount pileupCount) {
		for (int baseIndex : pileupCount.getBaseCallCount().getAlleles()) {
			substract(baseIndex, pileupCount);
		}
	}

	public void invert() {
		baseCallCount.invert();
		baseQualCount.invert();
	}

	public byte getReferenceBase() {
		return referenceBase;
	}

	@Override
	public void setReferenceBase(final byte referenceBase) {
		this.referenceBase = referenceBase;
	}

	@Override
	public int getCoverage() {
		return baseCallCount.getCoverage();
	}

	public String toString() {
		final StringBuilder sb = new StringBuilder();

		sb.append(baseCallCount.toString());
		sb.append(" Ref.: ");
		sb.append((char)referenceBase);
		sb.append('\n');
		sb.append(baseQualCount.toString());
		
		return sb.toString();
	}

}
