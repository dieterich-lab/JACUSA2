package lib.data.count.basecallquality;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import lib.phred2prob.Phred2Prob;
import lib.util.Base;

public class ArrayBaseCallQualityCount implements BaseCallQualityCount {

	private final int[][] baseCallQuals;

	public ArrayBaseCallQualityCount() {
		baseCallQuals = new int[Base.validValues().length][Phred2Prob.MAX_Q];
	}
	
	public ArrayBaseCallQualityCount(final int[][] baseCallQuals) {
		if (baseCallQuals.length != Base.validValues().length) {
			throw new IllegalStateException("Size of baseCalls != Base.validValues: " + baseCallQuals.length + " != " + Base.validValues().length);
		}
		for (final int[] quals : baseCallQuals) {
			if (quals.length != Phred2Prob.MAX_Q) {
				throw new IllegalStateException("Size of quals != Phred2Prob.MAX_Q: " + quals.length + " != " + Phred2Prob.MAX_Q);	
			}
			for (final int qual : quals) {
				if (qual < 0) {
					throw new IllegalStateException("qual must be >= 0: " + qual);	
				}
			}
		}
		this.baseCallQuals = baseCallQuals;
	}
	
	@Override
	public BaseCallQualityCount copy() {
		final int[][] tmp = new int[baseCallQuals.length][];
		for (int i = 0; i < tmp.length; ++i) {
			tmp[i] = Arrays.copyOf(baseCallQuals[i], baseCallQuals[i].length);
		}
		return new ArrayBaseCallQualityCount(tmp);
	}

	@Override
	public Set<Byte> getBaseCallQuality(final Base base) {
		final Set<Byte> ret = new TreeSet<Byte>();
		for (int baseQual = 0; baseQual < Phred2Prob.MAX_Q; baseQual++) {
			if (baseCallQuals[base.getIndex()][baseQual] > 0) {
				ret.add((byte)baseQual);
			}
		}
		return ret;
	}
	
	public int getBaseCallQuality(final Base base, byte baseQual) {
		return baseCallQuals[base.getIndex()][baseQual];
	}

	@Override
	public ArrayBaseCallQualityCount increment(Base base, byte baseQual) {
		baseCallQuals[base.getIndex()][baseQual]++;
		return this;
	}

	@Override
	public ArrayBaseCallQualityCount clear() {
		for (final int[] qual2cout : baseCallQuals) {
			Arrays.fill(qual2cout, 0);
		}
		return this;
	}

	@Override
	public ArrayBaseCallQualityCount set(final Base base, byte baseQual, int count) {
		baseCallQuals[base.getIndex()][baseQual] = count;
		return this;
	}

	@Override
	public ArrayBaseCallQualityCount add(final Base base, BaseCallQualityCount baseCallQualCount) {
		add(base, base, baseCallQualCount);
		return this;
	}

	@Override
	public ArrayBaseCallQualityCount add(final Set<Base> alleles, BaseCallQualityCount baseCallQualCount) {
		for (final Base base : alleles) {
			add(base, baseCallQualCount);
		}
		return this;
	}

	@Override
	public ArrayBaseCallQualityCount add(final Base dest, final Base src, final BaseCallQualityCount baseCallQualCount) {
		for (final byte baseQual : baseCallQualCount.getBaseCallQuality(src)) {
			final int countDest = getBaseCallQuality(dest, baseQual);
			final int countSrc = baseCallQualCount.getBaseCallQuality(src, baseQual);
			set(dest, baseQual, countDest + countSrc);
		}
		return this;
	}

	@Override
	public ArrayBaseCallQualityCount subtract(final Base base, final BaseCallQualityCount baseCallQualCount) {
		subtract(base, base, baseCallQualCount);
		return this;
	}

	@Override
	public ArrayBaseCallQualityCount subtract(final Base dest, final Base src, final BaseCallQualityCount baseCallQualCount) {
		for (final byte baseQual : baseCallQualCount.getBaseCallQuality(src)) {
			final int countDest = getBaseCallQuality(dest, baseQual);
			final int countSrc = baseCallQualCount.getBaseCallQuality(src, baseQual);
			set(dest, baseQual, countDest - countSrc);
		}
		return this;
	}

	@Override
	public ArrayBaseCallQualityCount subtract(final Set<Base> alleles, final BaseCallQualityCount baseCallQualCount) {
		for (final Base base : alleles) {
			subtract(base, baseCallQualCount);
		}
		return this;
	}

	@Override
	public ArrayBaseCallQualityCount invert() {
		for (final Base base : new Base[] {Base.A, Base.C}) {
			final Base complement = base.getComplement();
			if (getBaseCallQuality(base).size() == 0 && getBaseCallQuality(complement).size() == 0) {
				continue;
			}
			final int[] tmpCount 					= baseCallQuals[base.getIndex()];
			baseCallQuals[base.getIndex()]		= baseCallQuals[complement.getIndex()];
			baseCallQuals[complement.getIndex()]	= tmpCount;
		}
		return this;
	}

	@Override
	public Set<Base> getAlleles() {
		final Set<Base> alleles = new HashSet<Base>();
		for (final Base base : Base.validValues()) {
			if (getBaseCallQuality(base).size() > 0) {
				alleles.add(base);
			}
		}
		return alleles;
	}

	@Override
	public String toString() {
		return toString(this);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || ! (obj instanceof ArrayBaseCallQualityCount)) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		
		final ArrayBaseCallQualityCount bcqc = (ArrayBaseCallQualityCount)obj;
		return baseCallQuals.equals(bcqc.baseCallQuals);
	}
	
	@Override
	public int hashCode() {
		return baseCallQuals.hashCode();
	}
	
	/*
	 * Builder and Parser
	 */
	
	public static class Parser extends BaseCallQualityCount.AbstractParser {
		
		public Parser() {
			super();
		}
		
		public Parser(final char baseCallSep, final char qualSep, final char empty) {
			super(baseCallSep, qualSep, empty);
		}
		
		@Override
		public ArrayBaseCallQualityCount parse(String s) {
			final ArrayBaseCallQualityCount bcqc = new ArrayBaseCallQualityCount();
			parse(s, bcqc);
			return bcqc;
		}
		
	}
	
}
