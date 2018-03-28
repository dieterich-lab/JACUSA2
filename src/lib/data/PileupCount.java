package lib.data;

import java.util.Arrays;

import lib.cli.options.BaseCallConfig;
import lib.data.has.HasBaseCallCount;
import lib.data.has.HasCoverage;
import lib.data.has.HasReferenceBase;
import lib.phred2prob.Phred2Prob;

public class PileupCount 
implements HasBaseCallCount, HasCoverage, HasReferenceBase {

	private byte referenceBase;

	private BaseCallCount baseCallCount;
	
	// container
	private byte[][] base2qual;
	private byte[] minQual;
	
	public PileupCount() {
		referenceBase = 'N';
		
		baseCallCount = new BaseCallCount();

		base2qual	= new byte[BaseCallConfig.BASES.length][Phred2Prob.MAX_Q];
		minQual		= new byte[BaseCallConfig.BASES.length];
		Arrays.fill(minQual, Phred2Prob.MAX_Q);
	}

	public PileupCount(final byte referenceBase, final int[] baseCount, final byte[][] base2qual, byte[] minMapq) {
		this.referenceBase = referenceBase;

		baseCallCount = new BaseCallCount(baseCount);
		this.base2qual = base2qual;
		this.minQual = minMapq;
	}
	
	public PileupCount(final PileupCount pileupCount) {
		this();

		this.referenceBase = pileupCount.referenceBase;
		baseCallCount = pileupCount.baseCallCount.copy();

		for (int baseIndex : baseCallCount.getAlleles()) {
			System.arraycopy(pileupCount.base2qual[baseIndex], 0, base2qual[baseIndex], 0, pileupCount.base2qual[baseIndex].length);
		}
		System.arraycopy(pileupCount.minQual,0, minQual, 0, pileupCount.minQual.length);
	}

	public PileupCount copy() {
		return new PileupCount(this);
	}
	
	public BaseCallCount getBaseCallCount() {
		return baseCallCount;
	}

	public int getQualCount(final int baseIndex, final int qualIndex) {
		return base2qual[baseIndex][qualIndex];
	}
	
	public void add(final int baseIndex, final byte qual) {
		baseCallCount.increment(baseIndex);
		base2qual[baseIndex][qual]++;
		if (minQual[baseIndex] > qual) {
			minQual[baseIndex] = qual;
		}
	}
		
	public void add(final PileupCount pileupCount) {
		for (int baseIndex : pileupCount.getBaseCallCount().getAlleles()) {
			add(baseIndex, pileupCount);
		}
	}

	public void add(final int baseIndex, final PileupCount pileupCount) {
		baseCallCount.add(baseIndex, pileupCount.getBaseCallCount());

		for (int qualIndex = pileupCount.minQual[baseIndex]; qualIndex < Phred2Prob.MAX_Q ; ++qualIndex) {
			if (pileupCount.base2qual[baseIndex][qualIndex] > 0) {
				base2qual[baseIndex][qualIndex] += pileupCount.base2qual[baseIndex][qualIndex];
				if (pileupCount.minQual[baseIndex] < minQual[baseIndex]) {
					minQual[baseIndex] = pileupCount.minQual[baseIndex];
				}
			}
		}
	}

	public void add(final int baseIndex1, final int baseIndex2, final PileupCount pileupCount) {
		baseCallCount.add(baseIndex1, baseIndex2, pileupCount.getBaseCallCount());
		
		for (int qualIndex = pileupCount.minQual[baseIndex2]; qualIndex < Phred2Prob.MAX_Q ; ++qualIndex) {
			if (pileupCount.base2qual[baseIndex2][qualIndex] > 0) {
				base2qual[baseIndex1][qualIndex] += pileupCount.base2qual[baseIndex2][qualIndex];
				if (pileupCount.minQual[baseIndex2] < minQual[baseIndex2]) {
					minQual[baseIndex2] = pileupCount.minQual[baseIndex2];
				}
			}
		}
	}

	public void substract(final int baseIndex, final PileupCount pileupCount) {
		baseCallCount.substract(baseIndex, pileupCount.getBaseCallCount());

		for (int qualIndex = pileupCount.minQual[baseIndex]; qualIndex < Phred2Prob.MAX_Q ; ++qualIndex) {
			if (pileupCount.minQual[baseIndex] == 0) {
				minQual[baseIndex] = Phred2Prob.MAX_Q;
			}
		}
	}

	public void substract(final int baseIndex1, final int baseIndex2, final PileupCount pileupCount) {
		baseCallCount.substract(baseIndex1, baseIndex2, pileupCount.getBaseCallCount());

		for (int qualIndex = pileupCount.minQual[baseIndex2]; qualIndex < Phred2Prob.MAX_Q ; ++qualIndex) {
			if (pileupCount.base2qual[baseIndex2][qualIndex] > 0) {
				base2qual[baseIndex1][qualIndex] -= pileupCount.base2qual[baseIndex2][qualIndex];
				if (pileupCount.minQual[baseIndex1] == 0) {
					minQual[baseIndex2] = Phred2Prob.MAX_Q;
				}
			}
		}
	}
	
	public void substract(final PileupCount pileupCount) {
		for (int baseIndex : pileupCount.getBaseCallCount().getAlleles()) {
			substract(baseIndex, pileupCount);
		}
	}

	public void invert() {
		byte[][] tmpQualCount = new byte[BaseCallConfig.BASES.length][Phred2Prob.MAX_Q];
		
		for (int baseIndex : baseCallCount.getAlleles()) {
			int complementaryBaseIndex = BaseCallConfig.BASES.length - baseIndex - 1;  
			// invert qualCount
			tmpQualCount[complementaryBaseIndex] = base2qual[baseIndex];
		}

		baseCallCount.invert();
		base2qual = tmpQualCount;
	}

	public byte getReferenceBase() {
		return referenceBase;
	}
	
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

		return sb.toString();
	}
	
}
