package lib.estimate;

import java.util.Arrays;

import lib.stat.nominal.NominalData;
import lib.stat.sample.EstimationSample;
import lib.util.Info;
import lib.util.MathUtil;

import org.apache.commons.math3.special.Gamma;

/**
 * TODO comments
 */
public class MinkaEstimateDirMultAlpha {

	private final MinkaParameter minkaEstimateParameter;

	private double[] tmpRowWiseSums;
	
	public MinkaEstimateDirMultAlpha(final MinkaParameter minkaEstimateParameter) {
		this.minkaEstimateParameter = minkaEstimateParameter;
	}
	
	/**
	 * estimate alpha and returns log-lik
	 * TODO comments
	 * @param 
	 * @param estimateInfo
	 * @param backtrack
	 * @return
	 * 
	 * Tested in test.lib.estimate.MinkaEstimateDirMultAlphaTest
	 */
	public boolean maximizeLogLikelihood(final EstimationSample estimationSample, final Info estimateInfo, final boolean backtrack) {
		final NominalData nominalData 	= estimationSample.getNominalData();
		final int categories 			= nominalData.getCategories();  
		
		final double localSums[] 		= getRowWiseSums(nominalData);
		
		boolean converged 				= false;

		// container see Minka
		double[] gradient 	= new double[categories];
		double[] Q 			= new double[categories];
		double b;
		double z;
		// holds pre-computed value
		double summedAlphaOld;
		double digammaSummedAlphaOld;
		double trigammaSummedAlphaOld;
		// log-likelihood
		double loglikOld = Double.NEGATIVE_INFINITY;
		
		// maximize
		while (estimationSample.getIteration() < estimationSample.getMaxIterations() && ! converged) {
			// init alpha new
			double[] alphaNew = new double[categories];
			Arrays.fill(alphaNew, 0.0);
			
			// pre-compute
			summedAlphaOld 			= MathUtil.sum(estimationSample.getAlpha());
			digammaSummedAlphaOld 	= Gamma.digamma(summedAlphaOld);
			trigammaSummedAlphaOld 	= Gamma.trigamma(summedAlphaOld);

			// reset
			b = 0.0;
			double b_DenominatorSum = 0.0;
			for (int i = 0; i < categories; i++) {
				// reset
				gradient[i] = 0.0;
				Q[i] = 0.0;

				for (int replicateIndex = 0; replicateIndex < nominalData.getReplicates(); ++replicateIndex) {
					// calculate gradient
					gradient[i] += digammaSummedAlphaOld;
					gradient[i] -= Gamma.digamma(localSums[replicateIndex] + summedAlphaOld);
					// 
					gradient[i] += Gamma.digamma(nominalData.getReplicate(replicateIndex)[i] + estimationSample.getAlpha()[i]);
					gradient[i] -= Gamma.digamma(estimationSample.getAlpha()[i]);

					// calculate Q
					Q[i] += Gamma.trigamma(nominalData.getReplicate(replicateIndex)[i] + estimationSample.getAlpha()[i]);
					Q[i] -= Gamma.trigamma(estimationSample.getAlpha()[i]);
				}

				// calculate b
				b += gradient[i] / Q[i];
				b_DenominatorSum += 1.0 / Q[i];
			}

			// calculate z
			z = 0.0;
			for (int replicateIndex = 0; replicateIndex < nominalData.getReplicates(); ++replicateIndex) {
				z += trigammaSummedAlphaOld;
				z -= Gamma.trigamma(localSums[replicateIndex] + summedAlphaOld);
			}
			// calculate b cont.
			b = b / (1.0 / z + b_DenominatorSum);

			loglikOld = getLogLikelihood(estimationSample.getAlpha(), nominalData);
			
			// try update alphaNew
			boolean admissible = true; 		
			for (int i = 0; i < categories; ++i) {
				alphaNew[i] = estimationSample.getAlpha()[i] - (gradient[i] - b) / Q[i];

				if (alphaNew[i] < 0.0) {
					admissible = false;
				}
			}

			// check if alpha negative
			if (! admissible) {
				if (backtrack) {
					estimateInfo.add("backtrack" + estimationSample.getId(), Integer.toString(estimationSample.getIteration()));
					alphaNew = backtracking(estimationSample.getAlpha(), gradient, b_DenominatorSum, Q);
					if (alphaNew == null) {
						this.tmpRowWiseSums = null;
						return false;
					}
				} else {
					estimateInfo.add("reset" + estimationSample.getId(), Integer.toString(estimationSample.getIteration()));
					this.tmpRowWiseSums = null;
					return false;
				}
				// update value
				estimationSample.add(alphaNew, getLogLikelihood(alphaNew, nominalData));
			} else {
				// update value
				estimationSample.add(alphaNew, getLogLikelihood(alphaNew, nominalData));

				// check if converged
				double delta = Math.abs(estimationSample.getLogLikelihood() - loglikOld);
				if (delta  <= minkaEstimateParameter.getEpsilon()) {
					converged = true;
				}
			}
		}

		// reset
		this.tmpRowWiseSums = null;
		return true;
	}
	
	private double[] getRowWiseSums(final NominalData dirMultData) {
		if (tmpRowWiseSums == null) {
			tmpRowWiseSums = dirMultData.getRowWiseSums();
		}
		return tmpRowWiseSums;
	}
	
	// calculate likelihood
	/**
	 * 
	 * @param alpha
	 * @param dirMultData
	 * @return
	 * 
	 * Tested in test.lib.estimate.MinkaEstimateDirMultAlphaTest
	 */
	public double getLogLikelihood(final double[] alpha, final NominalData dirMultData) {
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
