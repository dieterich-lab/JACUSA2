package jacusa.estimate;

import java.util.Arrays;

import lib.data.count.PileupCount;
import lib.phred2prob.Phred2Prob;

// posterior estimation
// p(p|D) ~ D(n + alpha)
// use for zero replicate
/**
 * TODO comments
 */
public class BayesEstimateParameters extends AbstractEstimateParameters {

	private final double initialAlphaNull;
	
	public BayesEstimateParameters(final double initialAlphaNull, final Phred2Prob phred2Prob) {
		super("bayes", "Bayes estimate (n + alpha)", phred2Prob);
		this.initialAlphaNull = initialAlphaNull;
	}

	@Override
	public double[] estimateAlpha(int[] baseIs, PileupCount[] pileupCounts) {
		// use initial alpha to init
		final double[] alpha = new double[baseIs.length];
		if (initialAlphaNull > 0.0) {
			Arrays.fill(alpha, initialAlphaNull / (double)baseIs.length);
		} else {
			Arrays.fill(alpha, 0.0);
		}

		for (final PileupCount pileupCount : pileupCounts) {
			double[] v = phred2Prob.colSumProb(baseIs, pileupCount);
			for (int baseI : baseIs) {
				alpha[baseI] += v[baseI];
			}
		}

		return alpha;
	}

	@Override
	public double[][] probabilityMatrix(int[] baseIs, PileupCount[] pileupCount) {
		final double[][] probs = new double[pileupCount.length][baseIs.length];

		for (int pileupIndex = 0; pileupIndex < pileupCount.length; ++pileupIndex) {
			// sum the probabilities giving alpha 
			probs[pileupIndex] = phred2Prob.colMeanProb(baseIs, pileupCount[pileupIndex]);
		}

		return probs;
	}

}