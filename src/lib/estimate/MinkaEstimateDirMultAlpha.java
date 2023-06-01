package lib.estimate;

import java.util.Arrays;

import lib.stat.estimation.EstimationContainer;
import lib.stat.nominal.NominalData;
import lib.util.Info;
import lib.util.MathUtil;

import org.apache.commons.math3.special.Gamma;

/**
 * TODO add comments
 */
public class MinkaEstimateDirMultAlpha {
	
	private final MinkaParameter minkaEstPrm;
	
	// private double[] tmpRowWiseSums;
	
	public MinkaEstimateDirMultAlpha(final MinkaParameter minkaEstimateParameter) {
		this.minkaEstPrm = minkaEstimateParameter;
	}
	
	/**
	 * estimate alpha and returns log-lik
	 * TODO add comments
	 * @param 
	 * @param estimateInfo
	 * @param backtrack
	 * @return
	 * 
	 * Tested in test.lib.estimate.MinkaEstimateDirMultAlphaTest
	 */
	public boolean maximizeLogLikelihood(final EstimationContainer estContainer, final Info estimateInfo, final boolean backtrack) {
		final NominalData nominalData 	= estContainer.getNominalData();
		final int categories 			= nominalData.getCategories();  
		
		final double[] localSums 		= getRowWiseSums(nominalData);
		
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
		while (estContainer.getIteration() < estContainer.getMaxIterations() && ! converged) {
			// init alpha new
			double[] alphaNew = new double[categories];
			Arrays.fill(alphaNew, 0.0);
			
			// pre-compute
			summedAlphaOld 			= MathUtil.sum(estContainer.getAlpha());
			digammaSummedAlphaOld 	= Gamma.digamma(summedAlphaOld);
			trigammaSummedAlphaOld 	= Gamma.trigamma(summedAlphaOld);

			// reset
			b = 0.0;
			double b_DenominatorSum = 0.0;
			for (int i = 0; i < categories; i++) {
				// reset
				gradient[i] = 0.0;
				Q[i] = 0.0;

				for (int replicateI = 0; replicateI < nominalData.getReplicates(); ++replicateI) {
					// calculate gradient
					gradient[i] += digammaSummedAlphaOld;
					gradient[i] -= Gamma.digamma(localSums[replicateI] + summedAlphaOld);
					// 
					gradient[i] += Gamma.digamma(nominalData.getReplicate(replicateI)[i] + estContainer.getAlpha()[i]);
					gradient[i] -= Gamma.digamma(estContainer.getAlpha()[i]);

					// calculate Q
					Q[i] += Gamma.trigamma(nominalData.getReplicate(replicateI)[i] + estContainer.getAlpha()[i]);
					Q[i] -= Gamma.trigamma(estContainer.getAlpha()[i]);
				}

				// calculate b
				b += gradient[i] / Q[i];
				b_DenominatorSum += 1.0 / Q[i];
			}

			// calculate z
			z = 0.0;
			for (int replicateI = 0; replicateI < nominalData.getReplicates(); ++replicateI) {
				z += trigammaSummedAlphaOld;
				z -= Gamma.trigamma(localSums[replicateI] + summedAlphaOld);
			}
			// calculate b cont.
			b = b / (1.0 / z + b_DenominatorSum);

			loglikOld = getLogLikelihood(estContainer.getAlpha(), nominalData);
			
			// try update alphaNew
			boolean admissible = true; 		
			for (int i = 0; i < categories; ++i) {
				alphaNew[i] = estContainer.getAlpha()[i] - (gradient[i] - b) / Q[i];

				if (alphaNew[i] < 0.0) {
					admissible = false;
				}
			}

			// check if alpha negative
			if (! admissible) {
				if (backtrack) {
					estimateInfo.add("backtrack" + estContainer.getId(), Integer.toString(estContainer.getIteration()));
					alphaNew = backtracking(estContainer.getAlpha(), gradient, b_DenominatorSum, Q);
					if (alphaNew == null) {
						// this.tmpRowWiseSums = null;
						return false;
					}
				} else {
					estimateInfo.add("reset" + estContainer.getId(), Integer.toString(estContainer.getIteration()));
					// this.tmpRowWiseSums = null;
					return false;
				}
				// update value
				estContainer.add(alphaNew, getLogLikelihood(alphaNew, nominalData));
			} else {
				// update value
				estContainer.add(alphaNew, getLogLikelihood(alphaNew, nominalData));

				// check if converged
				double delta = Math.abs(estContainer.getLogLikelihood() - loglikOld);
				if (delta  <= minkaEstPrm.getEpsilon()) {
					converged = true;
				}
			}
		}

		// reset
		// this.tmpRowWiseSums = null;
		return true;
	}
	
	private double[] getRowWiseSums(final NominalData dirMultData) {
		/* FIXME
		if (tmpRowWiseSums == null) {
			tmpRowWiseSums = dirMultData.getRowWiseSums();
		}
		return tmpRowWiseSums;
		*/
		
		return dirMultData.getRowWiseSums();
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

		for (int replicateI = 0; replicateI < replicates.length; replicateI++) {
			logLikelihood += Gamma.logGamma(alphaSum);
			logLikelihood -= Gamma.logGamma(replicates[replicateI] + alphaSum);

			for (int i = 0; i < dirMultData.getCategories(); ++i) {
				logLikelihood += Gamma.logGamma(dirMultData.getReplicate(replicateI)[i] + alpha[i]);
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
