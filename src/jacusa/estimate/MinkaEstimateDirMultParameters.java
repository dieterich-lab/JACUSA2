package jacusa.estimate;

import java.util.Arrays;

import lib.cli.options.Base;
import lib.util.Info;
import lib.util.MathUtil;

import org.apache.commons.math3.special.Gamma;

/**
 * TODO comments
 */
public class MinkaEstimateDirMultParameters extends MinkaEstimateParameters {

	private final static double EPSILON = 0.001;
	
	private double[] tmpCoverages;
	
	public MinkaEstimateDirMultParameters() {
		super();
	}
	
	// estimate alpha and returns loglik
	public double maximizeLogLikelihood(
			final String condition,
			final double[] alphaOld, 
			final Base[] bases, 
			final double[][] dataMatrix,
			final Info estimateInfo,
			final boolean backtrack) {
		iterations = 0;

		final double localCoverages[] = getCoverages(bases, dataMatrix);
		
		boolean converged = false;

		// final int baseN = baseConfig.getBases().length;
		final int baseN = alphaOld.length;
		
		// init alpha new
		double[] alphaNew = new double[baseN];
		Arrays.fill(alphaNew, 0.0);

		// container see Minka
		double[] gradient = new double[baseN];
		double[] Q = new double[baseN];
		double b;
		double z;
		// holds pre-computed value
		double summedAlphaOld;
		double digammaSummedAlphaOld;
		double trigammaSummedAlphaOld;
		// log-likelihood
		double loglikOld = Double.NEGATIVE_INFINITY;
		double loglikNew = Double.NEGATIVE_INFINITY;

		int pileupN = dataMatrix.length;

		reset = false;
		
		// maximize
		while (iterations < maxIterations && ! converged) {
			// pre-compute
			summedAlphaOld = MathUtil.sum(alphaOld);
			digammaSummedAlphaOld = digamma(summedAlphaOld);
			trigammaSummedAlphaOld = trigamma(summedAlphaOld);

			// reset
			b = 0.0;
			double b_DenominatorSum = 0.0;
			for (final Base base : bases) {
				// reset
				gradient[base.getIndex()] = 0.0;
				Q[base.getIndex()] = 0.0;

				// System.out.println("baseI: " + baseI);
				for (int pileupI = 0; pileupI < pileupN; ++pileupI) {
					// calculate gradient
					gradient[base.getIndex()] += digammaSummedAlphaOld;
					gradient[base.getIndex()] -= digamma(localCoverages[pileupI] + summedAlphaOld);
					// 
					gradient[base.getIndex()] += digamma(dataMatrix[pileupI][base.getIndex()] + alphaOld[base.getIndex()]);
					gradient[base.getIndex()] -= digamma(alphaOld[base.getIndex()]);

					// calculate Q
					Q[base.getIndex()] += trigamma(dataMatrix[pileupI][base.getIndex()] + alphaOld[base.getIndex()]);
					Q[base.getIndex()] -= trigamma(alphaOld[base.getIndex()]);
				}

				// calculate b
				b += gradient[base.getIndex()] / Q[base.getIndex()];
				b_DenominatorSum += 1.0 / Q[base.getIndex()];
			}

			// calculate z
			z = 0.0;
			for (int pileupI = 0; pileupI < pileupN; ++pileupI) {
				z += trigammaSummedAlphaOld;
				z -= trigamma(localCoverages[pileupI] + summedAlphaOld);
			}
			// calculate b cont.
			b = b / (1.0 / z + b_DenominatorSum);

			loglikOld = getLogLikelihood(alphaOld, bases, dataMatrix);
			
			// try update alphaNew
			boolean admissible = true; 		
			for (final Base base : bases) {
				alphaNew[base.getIndex()] = alphaOld[base.getIndex()] - (gradient[base.getIndex()] - b) / Q[base.getIndex()];

				if (alphaNew[base.getIndex()] < 0.0) {
					admissible = false;
				}
			}
			// check if alpha negative
			if (! admissible) {
				if (backtrack) {
					estimateInfo.add("backtrack" + condition, Integer.toString(iterations));
					alphaNew = backtracking(alphaOld, bases, gradient, b_DenominatorSum, Q);
					if (alphaNew == null) {
						reset = true;
						this.tmpCoverages = null;
						return Double.NaN;
					}
				} else {
					estimateInfo.add("reset" + condition, Integer.toString(iterations));
					reset = true;
					this.tmpCoverages = null;
					return Double.NaN;
				}
			} else {
				// calculate log-likelihood for new alpha(s)
				loglikNew = getLogLikelihood(alphaNew, bases, dataMatrix);
	
				// check if converged
				double delta = Math.abs(loglikNew - loglikOld);
				if (delta  <= EPSILON) {
					converged = true;
				}
			}

			// update value
			System.arraycopy(alphaNew, 0, alphaOld, 0, alphaNew.length);
			iterations++;	
		}

		// reset
		this.tmpCoverages = null;
		return loglikNew;
	}
	
	private double[] getCoverages(final Base[] bases, final double[][] pileupMatrix) {
		if (tmpCoverages == null) {
			int pileupN = pileupMatrix.length;
			tmpCoverages = new double[pileupN];
			for (int pileupI = 0; pileupI < pileupN; pileupI++) {
				double sum = 0.0;
				for (final Base base : bases) {
					sum += pileupMatrix[pileupI][base.getIndex()];
				}
				tmpCoverages[pileupI] = sum;
			}
		}

		return tmpCoverages;
	}
	
	// calculate likelihood
	protected double getLogLikelihood(
			final double[] alpha, 
			final Base[] bases, 
			final double[][] dataMatrix) {
		double logLikelihood = 0.0;
		final double alphaSum = MathUtil.sum(alpha);
		final double[] coverages = getCoverages(bases, dataMatrix);

		for (int dataIndex = 0; dataIndex < coverages.length; dataIndex++) {
			logLikelihood += Gamma.logGamma(alphaSum);
			logLikelihood -= Gamma.logGamma(coverages[dataIndex] + alphaSum);

			for (final Base base : bases) {
				logLikelihood += Gamma.logGamma(dataMatrix[dataIndex][base.getIndex()] + alpha[base.getIndex()]);
				logLikelihood -= Gamma.logGamma(alpha[base.getIndex()]);
			}
		}
		return logLikelihood;
	}

}
