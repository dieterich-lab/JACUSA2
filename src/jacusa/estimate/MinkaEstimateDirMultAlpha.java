package jacusa.estimate;

import java.util.Arrays;

import lib.stat.dirmult.DirMultData;
import lib.stat.dirmult.DirMultSample;
import lib.util.Info;
import lib.util.MathUtil;

import org.apache.commons.math3.special.Gamma;

/**
 * TODO comments
 */
public class MinkaEstimateDirMultAlpha {

	private final MinkaEstimateParameter minkaEstimateParameter;

	private double[] tmpRowWiseSums;
	
	public MinkaEstimateDirMultAlpha(final MinkaEstimateParameter minkaEstimateParameter) {
		this.minkaEstimateParameter = minkaEstimateParameter;
	}
	
	// estimate alpha and returns loglik
	public boolean maximizeLogLikelihood(final DirMultSample dirMultSample, final Info estimateInfo, final boolean backtrack) {
		final DirMultData dirMultData = dirMultSample.getDirMultData();
		final int categories = dirMultData.getCategories();  
		
		final double localSums[] = getRowWiseSums(dirMultData);
		
		boolean converged = false;

		// container see Minka
		double[] gradient = new double[categories];
		double[] Q = new double[categories];
		double b;
		double z;
		// holds pre-computed value
		double summedAlphaOld;
		double digammaSummedAlphaOld;
		double trigammaSummedAlphaOld;
		// log-likelihood
		double loglikOld = Double.NEGATIVE_INFINITY;
		
		// maximize
		while (dirMultSample.getIteration() < dirMultSample.getMaxIterations() && ! converged) {
			// init alpha new
			double[] alphaNew = new double[categories];
			Arrays.fill(alphaNew, 0.0);
			
			// pre-compute
			summedAlphaOld 			= MathUtil.sum(dirMultSample.getAlpha());
			digammaSummedAlphaOld 	= Gamma.digamma(summedAlphaOld);
			trigammaSummedAlphaOld 	= Gamma.trigamma(summedAlphaOld);

			// reset
			b = 0.0;
			double b_DenominatorSum = 0.0;
			for (int i = 0; i < dirMultSample.getDirMultData().getCategories(); i++) {
				// reset
				gradient[i] = 0.0;
				Q[i] = 0.0;

				for (int replicateIndex = 0; replicateIndex < dirMultData.getReplicates(); ++replicateIndex) {
					// calculate gradient
					gradient[i] += digammaSummedAlphaOld;
					gradient[i] -= Gamma.digamma(localSums[replicateIndex] + summedAlphaOld);
					// 
					gradient[i] += Gamma.digamma(dirMultData.getReplicate(replicateIndex)[i] + dirMultSample.getAlpha()[i]);
					gradient[i] -= Gamma.digamma(dirMultSample.getAlpha()[i]);

					// calculate Q
					Q[i] += Gamma.trigamma(dirMultData.getReplicate(replicateIndex)[i] + dirMultSample.getAlpha()[i]);
					Q[i] -= Gamma.trigamma(dirMultSample.getAlpha()[i]);
				}

				// calculate b
				b += gradient[i] / Q[i];
				b_DenominatorSum += 1.0 / Q[i];
			}

			// calculate z
			z = 0.0;
			for (int replicateIndex = 0; replicateIndex < dirMultData.getReplicates(); ++replicateIndex) {
				z += trigammaSummedAlphaOld;
				z -= Gamma.trigamma(localSums[replicateIndex] + summedAlphaOld);
			}
			// calculate b cont.
			b = b / (1.0 / z + b_DenominatorSum);

			loglikOld = getLogLikelihood(dirMultSample.getAlpha(), dirMultData);
			
			// try update alphaNew
			boolean admissible = true; 		
			for (int i = 0; i < categories; ++i) {
				alphaNew[i] = dirMultSample.getAlpha()[i] - (gradient[i] - b) / Q[i];

				if (alphaNew[i] < 0.0) {
					admissible = false;
				}
			}

			// check if alpha negative
			if (! admissible) {
				if (backtrack) {
					estimateInfo.add("backtrack" + dirMultSample.getId(), Integer.toString(dirMultSample.getIteration()));
					alphaNew = backtracking(dirMultSample.getAlpha(), gradient, b_DenominatorSum, Q);
					if (alphaNew == null) {
						this.tmpRowWiseSums = null;
						return false;
					}
				} else {
					estimateInfo.add("reset" + dirMultSample.getId(), Integer.toString(dirMultSample.getIteration()));
					this.tmpRowWiseSums = null;
					return false;
				}
				// update value
				dirMultSample.add(alphaNew, getLogLikelihood(alphaNew, dirMultData));
			} else {
				// update value
				dirMultSample.add(alphaNew, getLogLikelihood(alphaNew, dirMultData));

				// check if converged
				double delta = Math.abs(dirMultSample.getLogLikelihood() - loglikOld);
				if (delta  <= minkaEstimateParameter.getEpsilon()) {
					converged = true;
				}
			}
		}

		// reset
		this.tmpRowWiseSums = null;
		return true;
	}
	
	private double[] getRowWiseSums(final DirMultData dirMultData) {
		if (tmpRowWiseSums == null) {
			tmpRowWiseSums = dirMultData.getRowWiseSums();
		}
		return tmpRowWiseSums;
	}
	
	// calculate likelihood
	public double getLogLikelihood(final double[] alpha, final DirMultData dirMultData) {
		double logLikelihood 		= 0.0;
		final double alphaSum 		= MathUtil.sum(alpha);
		final double[] replicates 	= getRowWiseSums(dirMultData);

		for (int replicateIndex = 0; replicateIndex < replicates.length; replicateIndex++) {
			logLikelihood += Gamma.logGamma(alphaSum);
			logLikelihood -= Gamma.logGamma(replicates[replicateIndex] + alphaSum);

			for (int i = 0; i < dirMultData.getCategories(); ++i) {
				logLikelihood += Gamma.logGamma(dirMultData.getReplicate(replicateIndex)[i] + alpha[i]);
				logLikelihood -= Gamma.logGamma(alpha[i]);
			}
		}
		return logLikelihood;
	}

	protected double[] backtracking(
			final double[] alpha, 
			final double[] gradient, 
			final double b, 
			final double[] Q) {
		double[] alphaNew = new double[alpha.length];
		
		// try smaller newton steps
		double lamba = 1.0;
		// decrease by
		double offset = 0.1;

		while (lamba >= 0.0) {
			lamba = lamba - offset;

			boolean admissible = true;
			// adjust alpha with smaller newton step
			for (int i = 0; i < alpha.length; i++) {
				alphaNew[i] = alpha[i] - lamba * (gradient[i] - b) / Q[i];
				// check if admissible
				if (alphaNew[i] < 0.0) {
					admissible = false;
					break;
				}
			}

			if (admissible) {
				return alphaNew;
			}
		}

		// could not find alpha(s)
		return null;
	}
	
}
