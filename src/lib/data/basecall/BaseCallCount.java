package lib.data.basecall;

import java.util.Arrays;

import lib.cli.options.BaseCallConfig;

public class BaseCallCount {

	// container
	private int[] baseCallCount;

	public BaseCallCount() {
		baseCallCount 	= new int[BaseCallConfig.BASES.length];
	}

	public BaseCallCount(final int[] baseCallCount) {
		this();
		System.arraycopy(baseCallCount, 0, this.baseCallCount, 0, baseCallCount.length);
	}
	
	public BaseCallCount(final BaseCallCount baseCallCount) {
		this();
		
		System.arraycopy(baseCallCount.baseCallCount, 0, this.baseCallCount, 0, baseCallCount.baseCallCount.length);
	}

	public BaseCallCount copy() {
		return new BaseCallCount(this);
	}
	
	public int getCoverage() {
		int coverage = 0;
		
		for (final int c : baseCallCount) {
			coverage += c;
		}

		return coverage;
	}
	
	public int getBaseCallCount(final int baseIndex) {
		return baseCallCount[baseIndex];
	}
	
	public void increment(final int baseIndex) {
		baseCallCount[baseIndex]++;
	}
		
	public void add(final BaseCallCount baseQualCount) {
		for (int baseIndex = 0; baseIndex < baseQualCount.baseCallCount.length; ++baseIndex) {
			if (baseQualCount.baseCallCount[baseIndex] > 0) {
				add(baseIndex, baseQualCount);
			}
		}
	}

	public void add(final int baseIndex, final BaseCallCount baseQualCount) {
		baseCallCount[baseIndex] += baseQualCount.baseCallCount[baseIndex];
	}

	public void add(final int baseIndex1, final int baseIndex2, final BaseCallCount baseQualCount) {
		baseCallCount[baseIndex1] += baseQualCount.baseCallCount[baseIndex2];
	}
	
	public void substract(final int baseIndex, final BaseCallCount baseQualCount) {
		baseCallCount[baseIndex] -= baseQualCount.baseCallCount[baseIndex];
	}

	public void substract(final int baseIndex1, final int baseIndex2, final BaseCallCount baseQualCount) {
		baseCallCount[baseIndex1] -= baseQualCount.baseCallCount[baseIndex2];
	}
	
	public void substract(final BaseCallCount baseQualCount) {
		for (int baseIndex = 0; baseIndex < baseQualCount.baseCallCount.length; ++baseIndex) {
			if (baseCallCount[baseIndex] > 0) {
				substract(baseIndex, baseQualCount);
			}
		}
	}

	public void invert() {
		int[] tmpBaseCount = new int[BaseCallConfig.BASES.length];

		for (int baseIndex : getAlleles()) {
			int complementaryBaseIndex = baseCallCount.length - baseIndex - 1;  

			// invert base count
			tmpBaseCount[complementaryBaseIndex] = baseCallCount[baseIndex];
		}

		baseCallCount = tmpBaseCount;
	}
	
	public int[] getAlleles() {
		// make this allele
		int[] alleles = new int[BaseCallConfig.BASES.length];
		int n = 0;
	
		for (int baseIndex = 0; baseIndex < BaseCallConfig.BASES.length; ++baseIndex) {
			if (getBaseCallCount(baseIndex) > 0) {
				alleles[n] = baseIndex;
				++n;
			}
		}
		return Arrays.copyOf(alleles, n);
	}

	public String toString() {
		final StringBuilder sb = new StringBuilder();
		
		int i = 0;
		final int n = BaseCallConfig.BASES.length;
		sb.append("(");
		sb.append(BaseCallConfig.BASES[i]);
		++i;
		for (; i < n; ++i) {
			sb.append(", ");
			sb.append(BaseCallConfig.BASES[i]);
		}
		sb.append(") (");
		
		i = 0;
		sb.append(baseCallCount[i]);
		++i;
		for (; i < n; ++i) {
			sb.append(", ");
			sb.append(baseCallCount[i]);
		}
		sb.append(")");
		
		return sb.toString();
	}
	
}
