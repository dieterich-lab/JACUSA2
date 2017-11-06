package jacusa.data;

import java.util.Arrays;

import jacusa.phred2prob.Phred2Prob;

public class BaseQualCount {

	// container
	private int[] baseCount;
	private int[][] base2qual;
	private int[] minQual;

	public BaseQualCount() {
		baseCount 	= new int[BaseCallConfig.BASES.length];
		base2qual	= new int[BaseCallConfig.BASES.length][Phred2Prob.MAX_Q];
		minQual		= new int[BaseCallConfig.BASES.length];
		Arrays.fill(minQual, Phred2Prob.MAX_Q);
	}

	public BaseQualCount(final int[] baseCount, final int[][] base2qual, int[] minMapq) {
		this();
		
		System.arraycopy(baseCount, 0, this.baseCount, 0, baseCount.length);
		for (int baseI = 0; baseI < baseCount.length; ++baseI) {
			if (baseCount[baseI] > 0) {
				System.arraycopy(base2qual[baseI], 0, this.base2qual[baseI], 0, base2qual[baseI].length);
			}
		}
		System.arraycopy(minMapq, 0, this.minQual, 0, minMapq.length);
	}
	
	public BaseQualCount(BaseQualCount counts) {
		this();
		
		System.arraycopy(counts.baseCount, 0, this.baseCount, 0, counts.baseCount.length);
		for (int baseIndex : counts.getAlleles()) {
			if (counts.baseCount[baseIndex] > 0) {
				System.arraycopy(counts.base2qual[baseIndex], 0, base2qual[baseIndex], 0, counts.base2qual[baseIndex].length);
			}
		}
		System.arraycopy(counts.minQual,0, minQual, 0, counts.minQual.length);
	}

	public BaseQualCount copy() {
		return new BaseQualCount(this);
	}
	
	public int getCoverage() {
		int coverage = 0;
		
		for (int c : baseCount) {
			coverage += c;
		}

		return coverage;
	}

	public int getQualCount(final int baseIndex, final int qualIndex) {
		return base2qual[baseIndex][qualIndex];
	}
	
	public int getBaseCount(final int baseIndex) {
		return baseCount[baseIndex];
	}
	
	public void add(final int baseIndex, final int qualIndex) {
		baseCount[baseIndex]++;
		base2qual[baseIndex][qualIndex]++;
		minQual[baseIndex] = Math.max(qualIndex, minQual[baseIndex]);
	}
		
	public void add(final BaseQualCount baseQualCount) {
		for (int baseIndex = 0; baseIndex < baseQualCount.baseCount.length; ++baseIndex) {
			if (baseQualCount.baseCount[baseIndex] > 0) {
				add(baseIndex, baseQualCount);
			}
		}
	}

	public void add(final int baseIndex, final BaseQualCount baseQualCount) {
		baseCount[baseIndex] += baseQualCount.baseCount[baseIndex];

		for (int qualIndex = baseQualCount.minQual[baseIndex]; qualIndex < Phred2Prob.MAX_Q ; ++qualIndex) {
			if (baseQualCount.base2qual[baseIndex][qualIndex] > 0) {
				base2qual[baseIndex][qualIndex] += baseQualCount.base2qual[baseIndex][qualIndex];
			}
		}
	}

	public void add(final int baseIndex1, final int baseIndex2, final BaseQualCount baseQualCount) {
		baseCount[baseIndex1] += baseQualCount.baseCount[baseIndex2];

		for (int qualIndex = baseQualCount.minQual[baseIndex2]; qualIndex < Phred2Prob.MAX_Q ; ++qualIndex) {
			if (baseQualCount.base2qual[baseIndex2][qualIndex] > 0) {
				base2qual[baseIndex1][qualIndex] += baseQualCount.base2qual[baseIndex2][qualIndex];
			}
		}
	}
	
	public void substract(final int baseIndex, final BaseQualCount baseQualCount) {
		baseCount[baseIndex] -= baseQualCount.baseCount[baseIndex];

		for (int qualIndex = baseQualCount.minQual[baseIndex]; qualIndex < Phred2Prob.MAX_Q ; ++qualIndex) {
			if (baseQualCount.base2qual[baseIndex][qualIndex] > 0) {
				base2qual[baseIndex][qualIndex] -= baseQualCount.base2qual[baseIndex][qualIndex];
			}
		}
	}

	public void substract(final int baseIndex1, final int baseIndex2, final BaseQualCount baseQualCount) {
		baseCount[baseIndex1] -= baseQualCount.baseCount[baseIndex2];

		for (int qualIndex = baseQualCount.minQual[baseIndex2]; qualIndex < Phred2Prob.MAX_Q ; ++qualIndex) {
			if (baseQualCount.base2qual[baseIndex2][qualIndex] > 0) {
				base2qual[baseIndex1][qualIndex] -= baseQualCount.base2qual[baseIndex2][qualIndex];
			}
		}
	}
	
	public void substract(final BaseQualCount baseQualCount) {
		for (int baseIndex = 0; baseIndex < baseQualCount.baseCount.length; ++baseIndex) {
			if (baseCount[baseIndex] > 0) {
				substract(baseIndex, baseQualCount);
			}
		}
	}

	public void invert() {
		int[] tmpBaseCount = new int[BaseCallConfig.BASES.length];
		int[][] tmpQualCount = new int[BaseCallConfig.BASES.length][Phred2Prob.MAX_Q];
		
		for (int baseIndex : getAlleles()) {
			int complementaryBaseIndex = baseCount.length - baseIndex - 1;  

			// invert base count
			tmpBaseCount[complementaryBaseIndex] = baseCount[baseIndex];
			// invert qualCount
			tmpQualCount[complementaryBaseIndex] = base2qual[baseIndex];
		}

		baseCount = tmpBaseCount;
		base2qual = tmpQualCount;
	}
	
	public int[] getAlleles() {
		// make this allele
		int[] alleles = new int[BaseCallConfig.BASES.length];
		int n = 0;
	
		for (int baseIndex = 0; baseIndex < BaseCallConfig.BASES.length; ++baseIndex) {
			if (getBaseCount(baseIndex) > 0) {
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
		sb.append(baseCount[i]);
		++i;
		for (; i < n; ++i) {
			sb.append(", ");
			sb.append(baseCount[i]);
		}
		sb.append(")");
		
		return sb.toString();
	}
	
}
