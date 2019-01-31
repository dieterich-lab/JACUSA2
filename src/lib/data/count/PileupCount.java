package lib.data.count;

import jacusa.JACUSA;
import lib.data.Data;
import lib.data.count.PileupCount;
import lib.data.count.basecall.BaseCallCount;
import lib.data.count.basecall.UnmodifiableBaseCallCount;
import lib.data.count.basecallquality.BaseCallQualityCount;
import lib.util.Base;

public class PileupCount implements Data<PileupCount> {

	private static final long serialVersionUID = 1L;

	private BaseCallQualityCount baseCallQualCount;

	public PileupCount() {
		this(JACUSA.BCQC_FACTORY.create());
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
	
	public BaseCallCount getBaseCallCount() {
		final BaseCallCount bcc = JACUSA.BCC_FACTORY.create();
		for (final Base base : baseCallQualCount.getAlleles()) {
			int count = 0;
			for (final byte baseQual : baseCallQualCount.getBaseCallQuality(base)) {
				count += baseCallQualCount.getBaseCallQuality(base, baseQual);
			}
			bcc.set(base, count);
		}
		return new UnmodifiableBaseCallCount(bcc);
	}
	
	public BaseCallQualityCount getBaseCallQualityCount() {
		return baseCallQualCount;
	}

	public void add(final Base base, final byte baseQual) {
		baseCallQualCount.increment(base, baseQual);
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
		baseCallQualCount.add(dest, src, pileupCount.getBaseCallQualityCount());
	}

	public void substract(final Base base, final PileupCount pileupCount) {
		substract(base, base, pileupCount);
	}

	public void substract(final Base dest, final Base src, final PileupCount pileupCount) {
		baseCallQualCount.subtract(dest, src, pileupCount.getBaseCallQualityCount());
	}

	public void substract(final PileupCount pileupCount) {
		for (final Base base : pileupCount.getBaseCallCount().getAlleles()) {
			substract(base, pileupCount);
		}
	}

	public void invert() {
		baseCallQualCount.invert();
	}

	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(baseCallQualCount.toString());
		sb.append('\n');
		return sb.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || ! (obj instanceof PileupCount)) {
			return false;
		}
		if (obj == this) {
			return true;
		}

		final PileupCount pileupCount = (PileupCount)obj;
		return baseCallQualCount.equals(pileupCount.baseCallQualCount);
	}

	@Override
	public int hashCode() {
		int hash = 1;
		hash = 31 * hash + baseCallQualCount.hashCode();
		return hash;
	}
	
	public static class Parser implements lib.util.Parser<PileupCount> {

		private final BaseCallQualityCount.AbstractParser bcqcParser;
		
		public Parser(final BaseCallQualityCount.AbstractParser bcqcParser) {
			this.bcqcParser	= bcqcParser;
		}

		public String wrap(final PileupCount pileupCount) {
			final StringBuilder sb = new StringBuilder();
			if (pileupCount.getBaseCallCount().getCoverage() == 0) {
				sb.append(bcqcParser.getEmpty());	
			} else {
				sb.append(bcqcParser.wrap(pileupCount.getBaseCallQualityCount()));
			}
			return sb.toString();
		}

		@Override
		public PileupCount parse(String s) {
			if (s.equals(Character.toString(bcqcParser.getEmpty()))) {
				return new PileupCount();	
			}
			
			final BaseCallQualityCount bcqc = bcqcParser.parse(s);
			return new PileupCount(bcqc);
		}

	}
	
}
