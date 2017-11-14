package lib.phred2prob;

import java.util.Arrays;

import lib.cli.options.BaseCallConfig;
import lib.data.PileupCount;
import lib.data.has.hasPileupCount;
import lib.util.MathUtil;

public final class Phred2Prob {

	private final double[] phred2errerP;
	private final double[] phred2baseP;
	private final double[] phred2baseErrorP;

	// phred capped at 41
	public static final byte MAX_Q = 41 + 1; // some machines give phred score of 60 -> Prob of error: 10^-6 ?!
	private static Phred2Prob[] singles = new Phred2Prob[BaseCallConfig.BASES.length + 1];

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

	public double[] colSumCount(final int[] baseIndexs, final hasPileupCount o) {
		// container for accumulated probabilities 
		final double[] c = new double[BaseCallConfig.BASES.length];

		for (int baseIndex : baseIndexs) {
			final int count = o.getPileupCount().getBaseCallCount().getBaseCallCount(baseIndex);
			c[baseIndex] = count;
		}
		return c;		
	}

	/**
	 * Calculate a probability vector P for the pileup. |P| = |bases| 
	 */
	public double[] colSumProb(final int[] baseIs, final PileupCount pileupCount) {
		// container for accumulated probabilities 
		final double[] p = new double[BaseCallConfig.BASES.length];
		Arrays.fill(p, 0.0);

		for (int baseIndex : baseIs) {
			for (byte qual = 0 ; qual < Phred2Prob.MAX_Q; ++qual) {
				// number of bases with specific quality 
				final int count = pileupCount.getQualCount(baseIndex, qual);
				if (count > 0) {
					final double baseP = convert2P(qual);
					p[baseIndex] += (double)count * baseP;

					final double errorP = convert2errorP(qual) / (baseIs.length - 1);
					// distribute error probability
					for (int baseI2 : baseIs) {
						if (baseI2 != baseIndex) {
							p[baseI2] += (double)count * errorP;
						}
					}
				}
			}
		}
		
		return p;
	}

	public double[] colSumErrorProb(final int[] baseIs, final PileupCount pileupCount) {
		// container for accumulated probabilities 
		final double[] p = new double[BaseCallConfig.BASES.length];
		Arrays.fill(p, 0.0);

		for (int baseIndex : baseIs) {
			for (byte qual = 0 ; qual < Phred2Prob.MAX_Q; ++qual) {
				// number of bases with specific quality 
				final int count = pileupCount.getQualCount(baseIndex, qual);

				if (count > 0) {
					final double errorP = convert2errorP(qual) / (double)(baseIs.length - 1);

					// distribute error probability
					for (int baseI2 : baseIs) {
						if (baseI2 != baseIndex) {
							p[baseI2] += (double)count * errorP;
						}
					}
				}
			}
		}
		return p;		
	}

	public double[] colMeanErrorProb(final int[] baseIndexs, final PileupCount pileupCount) {
		// container for accumulated probabilities 
		final double[] p = colSumErrorProb(baseIndexs, pileupCount);
		
		for (int baseI : baseIndexs) {
			p[baseI] /= (double)pileupCount.getBaseCallCount().getCoverage();
		}
		
		return p;
	}

	public double[] colMeanProb(final int[] baseIs, final PileupCount pileupCount) {
		// container for accumulated probabilities 
		final double[] p = colSumProb(baseIs, pileupCount);
		double sum = MathUtil.sum(p);

		for(int baseI : baseIs) {
			p[baseI] /= sum;
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