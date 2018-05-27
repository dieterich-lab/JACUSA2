package lib.data.count;

import lib.cli.options.Base;
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

	public void add(final Base base, final byte baseQual) {
		baseCallCount.increment(base);
		baseQualCount.increment(base, baseQual);
	}
		
	public void merge(final PileupCount pileupCount) {
		for (final Base base : pileupCount.getBaseCallCount().getAlleles()) {
			add(base, pileupCount);
		}
	}
	
	public void add(final Base base, final PileupCount pileupCount) {
		add(base, base, pileupCount);
	}

	public void add(final Base dest, final Base src, final PileupCount pileupCount) {
		baseCallCount.add(dest, src, pileupCount.getBaseCallCount());
		baseQualCount.add(dest, src, pileupCount.getBaseCallQualityCount());
	}

	public void substract(final Base base, final PileupCount pileupCount) {
		substract(base, base, pileupCount);
	}

	public void substract(final Base dest, final Base src, final PileupCount pileupCount) {
		baseCallCount.add(dest, src, pileupCount.getBaseCallCount());
		baseQualCount.add(dest, src, pileupCount.getBaseCallQualityCount());
	}

	public void substract(final PileupCount pileupCount) {
		for (final Base base : pileupCount.getBaseCallCount().getAlleles()) {
			substract(base, pileupCount);
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
