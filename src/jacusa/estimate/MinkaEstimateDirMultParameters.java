package jacusa.estimate;

import java.util.Arrays;

import jacusa.util.Info;
import jacusa.util.MathUtil;

import org.apache.commons.math3.special.Gamma;

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
			final int[] baseIndexs, 
			final double[][] dataMatrix,
			final Info estimateInfo,
			final boolean backtrack) {
		iterations = 0;

		final double localCoverages[] = getCoverages(baseIndexs, dataMatrix);
		
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
			for (int baseI : baseIndexs) {
				// reset
				gradient[baseI] = 0.0;
				Q[baseI] = 0.0;

				// System.out.println("baseI: " + baseI);
				for (int pileupI = 0; pileupI < pileupN; ++pileupI) {
					// calculate gradient
					gradient[baseI] += digammaSummedAlphaOld;
					gradient[baseI] -= digamma(localCoverages[pileupI] + summedAlphaOld);
					// 
					gradient[baseI] += digamma(dataMatrix[pileupI][baseI] + alphaOld[baseI]);
					gradient[baseI] -= digamma(alphaOld[baseI]);

					// calculate Q
					Q[baseI] += trigamma(dataMatrix[pileupI][baseI] + alphaOld[baseI]);
					Q[baseI] -= trigamma(alphaOld[baseI]);
				}

				// calculate b
				b += gradient[baseI] / Q[baseI];
				b_DenominatorSum += 1.0 / Q[baseI];
			}

			// calculate z
			z = 0.0;
			for (int pileupI = 0; pileupI < pileupN; ++pileupI) {
				z += trigammaSummedAlphaOld;
				z -= trigamma(localCoverages[pileupI] + summedAlphaOld);
			}
			// calculate b cont.
			b = b / (1.0 / z + b_DenominatorSum);

			loglikOld = getLogLikelihood(alphaOld, baseIndexs, dataMatrix);
			
			// try update alphaNew
			boolean admissible = true; 		
			for (int baseI : baseIndexs) {
				alphaNew[baseI] = alphaOld[baseI] - (gradient[baseI] - b) / Q[baseI];

				if (alphaNew[baseI] < 0.0) {
					admissible = false;
				}
			}
			// check if alpha negative
			if (! admissible) {
				if (backtrack) {
					estimateInfo.add("backtrack" + condition, Integer.toString(iterations));
					alphaNew = backtracking(alphaOld, baseIndexs, gradient, b_DenominatorSum, Q);
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
				loglikNew = getLogLikelihood(alphaNew, baseIndexs, dataMatrix);
	
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
	
	private double[] getCoverages(final int[] baseIs, final double[][] pileupMatrix) {
		if (tmpCoverages == null) {
			int pileupN = pileupMatrix.length;
			tmpCoverages = new double[pileupN];
			for (int pileupI = 0; pileupI < pileupN; pileupI++) {
				double sum = 0.0;
				for (int baseI : baseIs) {
					sum += pileupMatrix[pileupI][baseI];
				}
				tmpCoverages[pileupI] = sum;
			}
		}

		return tmpCoverages;
	}
	
	// calculate likelihood
	protected double getLogLikelihood(
			final double[] alpha, 
			final int[] baseIndexs, 
			final double[][] dataMatrix) {
		double logLikelihood = 0.0;
		final double alphaSum = MathUtil.sum(alpha);
		final double[] coverages = getCoverages(baseIndexs, dataMatrix);

		for (int dataIndex = 0; dataIndex < coverages.length; dataIndex++) {
			logLikelihood += Gamma.logGamma(alphaSum);
			logLikelihood -= Gamma.logGamma(coverages[dataIndex] + alphaSum);

			for (int baseIndex : baseIndexs) {
				logLikelihood += Gamma.logGamma(dataMatrix[dataIndex][baseIndex] + alpha[baseIndex]);
				logLikelihood -= Gamma.logGamma(alpha[baseIndex]);
			}
		}
		return logLikelihood;
	}

}
