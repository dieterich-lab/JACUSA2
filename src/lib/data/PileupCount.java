package lib.data;

import java.util.Arrays;

import lib.cli.options.BaseCallConfig;
import lib.phred2prob.Phred2Prob;

public class PileupCount {

	private byte referenceBase;
	
	// container
	private int[] baseCount;
	private byte[][] base2qual;
	private byte[] minQual;

	public PileupCount() {
		referenceBase = 'N';
		
		baseCount 	= new int[BaseCallConfig.BASES.length];
		base2qual	= new byte[BaseCallConfig.BASES.length][Phred2Prob.MAX_Q];
		minQual		= new byte[BaseCallConfig.BASES.length];
		Arrays.fill(minQual, Phred2Prob.MAX_Q);
	}

	public PileupCount(final byte referenceBase, final int[] baseCount, final byte[][] base2qual, byte[] minMapq) {
		this();
		this.referenceBase = referenceBase;
		this.baseCount = baseCount;
		this.base2qual = base2qual;
		this.minQual = minMapq;
	}
	
	public PileupCount(final PileupCount pileupCount) {
		this();

		this.referenceBase = pileupCount.referenceBase;
		System.arraycopy(pileupCount.baseCount, 0, this.baseCount, 0, pileupCount.baseCount.length);
		for (int baseIndex : pileupCount.getAlleles()) {
			if (pileupCount.baseCount[baseIndex] > 0) {
				System.arraycopy(pileupCount.base2qual[baseIndex], 0, base2qual[baseIndex], 0, pileupCount.base2qual[baseIndex].length);
			}
		}
		System.arraycopy(pileupCount.minQual,0, minQual, 0, pileupCount.minQual.length);
	}

	public PileupCount copy() {
		return new PileupCount(this);
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
	
	public void add(final int baseIndex, final byte qual) {
		baseCount[baseIndex]++;
		base2qual[baseIndex][qual]++;
		if (minQual[baseIndex] > qual) {
			minQual[baseIndex] = qual;
		}
	}
		
	public void add(final PileupCount pileupCount) {
		for (int baseIndex = 0; baseIndex < pileupCount.baseCount.length; ++baseIndex) {
			if (pileupCount.baseCount[baseIndex] > 0) {
				add(baseIndex, pileupCount);
			}
		}
	}

	public void add(final int baseIndex, final PileupCount pileupCount) {
		baseCount[baseIndex] += pileupCount.baseCount[baseIndex];

		for (int qualIndex = pileupCount.minQual[baseIndex]; qualIndex < Phred2Prob.MAX_Q ; ++qualIndex) {
			if (pileupCount.base2qual[baseIndex][qualIndex] > 0) {
				base2qual[baseIndex][qualIndex] += pileupCount.base2qual[baseIndex][qualIndex];
			}
		}
	}

	public void add(final int baseIndex1, final int baseIndex2, final PileupCount pileupCount) {
		baseCount[baseIndex1] += pileupCount.baseCount[baseIndex2];

		for (int qualIndex = pileupCount.minQual[baseIndex2]; qualIndex < Phred2Prob.MAX_Q ; ++qualIndex) {
			if (pileupCount.base2qual[baseIndex2][qualIndex] > 0) {
				base2qual[baseIndex1][qualIndex] += pileupCount.base2qual[baseIndex2][qualIndex];
			}
		}
	}
	
	public void substract(final int baseIndex, final PileupCount pileupCount) {
		baseCount[baseIndex] -= pileupCount.baseCount[baseIndex];

		for (int qualIndex = pileupCount.minQual[baseIndex]; qualIndex < Phred2Prob.MAX_Q ; ++qualIndex) {
			if (pileupCount.base2qual[baseIndex][qualIndex] > 0) {
				base2qual[baseIndex][qualIndex] -= pileupCount.base2qual[baseIndex][qualIndex];
			}
		}
	}

	public void substract(final int baseIndex1, final int baseIndex2, final PileupCount pileupCount) {
		baseCount[baseIndex1] -= pileupCount.baseCount[baseIndex2];

		for (int qualIndex = pileupCount.minQual[baseIndex2]; qualIndex < Phred2Prob.MAX_Q ; ++qualIndex) {
			if (pileupCount.base2qual[baseIndex2][qualIndex] > 0) {
				base2qual[baseIndex1][qualIndex] -= pileupCount.base2qual[baseIndex2][qualIndex];
			}
		}
	}
	
	public void substract(final PileupCount pileupCount) {
		for (int baseIndex = 0; baseIndex < pileupCount.baseCount.length; ++baseIndex) {
			if (baseCount[baseIndex] > 0) {
				substract(baseIndex, pileupCount);
			}
		}
	}

	public void invert() {
		int[] tmpBaseCount = new int[BaseCallConfig.BASES.length];
		byte[][] tmpQualCount = new byte[BaseCallConfig.BASES.length][Phred2Prob.MAX_Q];
		
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

	public byte getReferenceBase() {
		return referenceBase;
	}
	
	public void setReferenceBase(final byte referenceBase) {
		this.referenceBase = referenceBase;
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
		sb.append(" Ref.: ");
		sb.append((char)referenceBase);
		
		return sb.toString();
	}
	
}
