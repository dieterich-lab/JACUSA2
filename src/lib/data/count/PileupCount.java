package lib.data.count;

import lib.data.Data;
import lib.data.count.PileupCount;
import lib.data.count.basecall.BaseCallCount;
import lib.data.count.basecall.UnmodifiableBCC;
import lib.data.count.basecallquality.BaseCallQualityCount;
import lib.util.Base;

public class PileupCount implements Data<PileupCount> {

	private static final long serialVersionUID = 1L;

	private BaseCallQualityCount baseCallQualCount;

	public PileupCount() {
		this(BaseCallQualityCount.create());
	}
	
	public PileupCount(final BaseCallQualityCount baseCallQualCount) {
		this.baseCallQualCount = baseCallQualCount;
	}
	
	public PileupCount(final PileupCount pileupCount) {
		this.baseCallQualCount 	= pileupCount.baseCallQualCount.copy();
	}
	
	public PileupCount copy() {
		return new PileupCount(this);
	}
	
	public BaseCallCount getBCC() {
		final BaseCallCount bcc = BaseCallCount.create();
		for (final Base base : baseCallQualCount.getAlleles()) {
			int count = 0;
			for (final byte baseQual : baseCallQualCount.getBaseCallQuality(base)) {
				count += baseCallQualCount.getBaseCallQuality(base, baseQual);
			}
			bcc.set(base, count);
		}
		return new UnmodifiableBCC(bcc);
	}
	
	public BaseCallQualityCount getBaseCallQualityCount() {
		return baseCallQualCount;
	}
	
	public void merge(final PileupCount pileupCount) {
		for (final Base base : pileupCount.getBCC().getAlleles()) {
			add(base, pileupCount);
		}
	}
	
	public void add(final Base base, final PileupCount pileupCount) {
		add(base, base, pileupCount);
	}

	public void add(final Base dest, final Base src, final PileupCount pileupCount) {
		baseCallQualCount.add(dest, src, pileupCount.getBaseCallQualityCount());
	}

	public void substract(final Base base, final PileupCount pileupCount) {
		substract(base, base, pileupCount);
	}

	public void substract(final Base dest, final Base src, final PileupCount pileupCount) {
		baseCallQualCount.subtract(dest, src, pileupCount.getBaseCallQualityCount());
	}

	public void substract(final PileupCount pileupCount) {
		for (final Base base : pileupCount.getBCC().getAlleles()) {
			substract(base, pileupCount);
		}
	}

	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(getBCC().toString());
		sb.append('\n');
		sb.append(baseCallQualCount.toString());
		sb.append('\n');
		return sb.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (! (obj instanceof PileupCount)) {
			return false;
		}
		if (obj == this) {
			return true;
		}

		final PileupCount pileupCount = (PileupCount)obj;
		return baseCallQualCount.specificEquals(pileupCount.baseCallQualCount);
	}

	@Override
	public int hashCode() {
		int hash = 1;
		hash = 31 * hash + baseCallQualCount.hashCode();
		return hash;
	}

}
