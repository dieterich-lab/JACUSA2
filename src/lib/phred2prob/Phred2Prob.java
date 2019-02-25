package lib.phred2prob;

import java.util.Arrays;

import htsjdk.samtools.util.SequenceUtil;
import lib.data.count.PileupCount;
import lib.util.Base;
import lib.util.MathUtil;

public final class Phred2Prob {

	private final double[] phred2errerP;
	private final double[] phred2baseP;
	private final double[] phred2baseErrorP;

	// phred capped at 41
	public static final byte MAX_Q = 41 + 1; // some machines give phred score of 60 -> Prob of error: 10^-6 ?!
	private static Phred2Prob[] singles = new Phred2Prob[SequenceUtil.VALID_BASES_UPPER.length + 1];

	private Phred2Prob(int n) {
		// pre-calculate probabilities
		final int min = 0;
		phred2errerP = new double[MAX_Q];
		phred2baseP = new double[MAX_Q];
		phred2baseErrorP = new double[MAX_Q];

		for(int i = min; i < MAX_Q; i++) {
			phred2errerP[i] = Math.pow(10.0, -(double)i / 10.0);
			phred2baseP[i] = 1.0 - phred2errerP[i];
			phred2baseErrorP[i] = phred2errerP[i] / (n - 1); // ignore the called base
		}
	}

	public double convert2errorP(byte qual) {
		qual =  qual > MAX_Q ? MAX_Q : qual; 
		return phred2errerP[qual];
	}

	public double convert2P(byte qual) {
		qual =  qual > MAX_Q ? MAX_Q : qual;
		return phred2baseP[qual];
	}

	public double[] colSumCount(final Base[] bases, final PileupCount o) {
		// container for accumulated counts 
		final double[] c = new double[Base.validValues().length];

		for (final Base base : bases) {
			final int count = o.getBaseCallCount().getBaseCall(base);
			c[base.getIndex()] = count;
		}
		return c;		
	}

	/**
	 * Calculate a probability vector P for the pileup. |P| = |bases| 
	 */
	public double[] colSumProb(final Base[] bases, final PileupCount o) {
		// container for accumulated probabilities 
		final double[] p = new double[Base.validValues().length];
		Arrays.fill(p, 0.0);

		for (final Base base: bases) {
			for (final byte baseQual : o.getBaseCallQualityCount().getBaseCallQuality(base)) {
				// number of bases with specific quality 
				final int count = o.getBaseCallQualityCount().getBaseCallQuality(base, baseQual);
				if (count > 0) {
					final double baseP = convert2P(baseQual);
					p[base.getIndex()] += (double)count * baseP;
					
					final double errorP = convert2errorP(baseQual) / (bases.length - 1);
					// distribute error probability
					for (final Base base2 : Base.getNonRefBases(base)) {
						p[base2.getIndex()] += (double)count * errorP;
					}
				}
			}
		}
		
		return p;
	}

	public double[] colSumErrorProb(final Base[] bases, final PileupCount pileupCount) {
		// container for accumulated probabilities 
		final double[] p = new double[Base.validValues().length];
		Arrays.fill(p, 0.0);

		for (final Base base : bases) {
			for (final byte baseQual : pileupCount.getBaseCallQualityCount().getBaseCallQuality(base)) {
				// number of bases with specific quality 
				final int count = pileupCount.getBaseCallQualityCount().getBaseCallQuality(base, baseQual);

				if (count > 0) {
					final double errorP = convert2errorP(baseQual) / (double)(bases.length - 1);
					// distribute error probability
					for (final Base base2 : Base.getNonRefBases(base)) {
						p[base2.getIndex()] += (double)count * errorP;
					}
				}
			}
		}

		return p;
	}

	public double[] colMeanErrorProb(final Base[] bases, final PileupCount pileupCount) {
		// container for accumulated probabilities 
		final double[] p = colSumErrorProb(bases, pileupCount);
		
		for (final Base base : bases) {
			p[base.getIndex()] /= (double)pileupCount.getBaseCallCount().getCoverage();
		}
		
		return p;
	}

	public double[] colMeanProb(final Base[] bases, final PileupCount o) {
		// container for accumulated probabilities 
		final double[] p = colSumProb(bases, o);
		double sum = MathUtil.sum(p);

		for(final Base base : bases) {
			p[base.getIndex()] /= sum;
		}
		
		return p;
	}

	public static Phred2Prob getInstance(int baseCount) {
		if (singles[baseCount] == null) {
			singles[baseCount] = new Phred2Prob(baseCount);
		}

		return singles[baseCount];
	}
	
}