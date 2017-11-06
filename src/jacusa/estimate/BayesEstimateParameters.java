package jacusa.estimate;

import jacusa.data.hasBaseQualCount;
import jacusa.phred2prob.Phred2Prob;

import java.util.Arrays;

// posterior estimation
// p(p|D) ~ D(n + alpha)
// use for zero replicate
public class BayesEstimateParameters extends AbstractEstimateParameters {

	private final double initialAlphaNull;
	
	public BayesEstimateParameters(final double initialAlphaNull, final Phred2Prob phred2Prob) {
		super("bayes", "Bayes estimate (n + alpha)", phred2Prob);
		this.initialAlphaNull = initialAlphaNull;
	}

	@Override
	public double[] estimateAlpha(int[] baseIs, hasBaseQualCount[] pileups) {
		// use initial alpha to init
		final double[] alpha = new double[baseIs.length];
		if (initialAlphaNull > 0.0) {
			Arrays.fill(alpha, initialAlphaNull / (double)baseIs.length);
		} else {
			Arrays.fill(alpha, 0.0);
		}

		for (final hasBaseQualCount pileup : pileups) {
			double[] v = phred2Prob.colSumProb(baseIs, pileup.getBaseQualCount());
			for (int baseI : baseIs) {
				alpha[baseI] += v[baseI];
			}
		}

		return alpha;
	}

	@Override
	public double[][] probabilityMatrix(int[] baseIs, hasBaseQualCount[] pileups) {
		final double[][] probs = new double[pileups.length][baseIs.length];

		for (int pileupIndex = 0; pileupIndex < pileups.length; ++pileupIndex) {
			// sum the probabilities giving alpha 
			probs[pileupIndex] = phred2Prob.colMeanProb(baseIs, pileups[pileupIndex].getBaseQualCount());
		}

		return probs;
	}

}