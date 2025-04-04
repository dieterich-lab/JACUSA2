package lib.estimate;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import lib.stat.estimation.ConditionEstimate;
import lib.stat.estimation.EstimationContainer;
import lib.stat.initalpha.AbstractAlphaInit;
import lib.stat.nominal.NominalData;
import lib.util.ExtendedInfo;
import lib.util.MathUtil;
import lib.util.Util;

import org.apache.commons.math3.special.Gamma;

/**
 * DOCUMENT
 */
public class MinkaEstimateDirMultAlpha {
	
	private final MinkaParameter minkaParameters;
	
	public MinkaEstimateDirMultAlpha(final MinkaParameter minkaParameter) {
		this.minkaParameters = minkaParameter;
	}
	
	/**
	 * Implementation of parameter estimation of a DirichletMultinomial based on Minka algorithm - estimate alpha and returns log-lik
	 * @param 
	 * @param estimateInfo
	 * @param backtrack
	 * @return
	 * 
	 * Tested in test.lib.estimate.MinkaEstimateDirMultAlphaTest
	 */
	public boolean maximizeLogLikelihood(final ConditionEstimate estimationContainer, final ExtendedInfo estimateInfo, final boolean backtrack) {
		final NominalData nominalData 	= estimationContainer.getNominalData();
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
		while (estimationContainer.getIteration() < estimationContainer.getMaxIterations() && ! converged) {
			// init alpha new
			double[] alphaNew = new double[categories];
			Arrays.fill(alphaNew, 0.0);
			
			// pre-compute
			summedAlphaOld 			= MathUtil.sum(estimationContainer.getAlpha());
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
					gradient[i] += Gamma.digamma(nominalData.getReplicate(replicateIndex)[i] + estimationContainer.getAlpha()[i]);
					gradient[i] -= Gamma.digamma(estimationContainer.getAlpha()[i]);

					// calculate Q
					Q[i] += Gamma.trigamma(nominalData.getReplicate(replicateIndex)[i] + estimationContainer.getAlpha()[i]);
					Q[i] -= Gamma.trigamma(estimationContainer.getAlpha()[i]);
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

			loglikOld = getLogLikelihood(estimationContainer.getAlpha(), nominalData);
			
			// try update alphaNew
			boolean admissible = true; 		
			for (int i = 0; i < categories; ++i) {
				alphaNew[i] = estimationContainer.getAlpha()[i] - (gradient[i] - b) / Q[i];

				if (alphaNew[i] < 0.0) {
					admissible = false;
				}
			}

			// check if alpha negative
			if (! admissible) {
				if (backtrack) {
					estimateInfo.add("backtrack" + estimationContainer.getID(), Integer.toString(estimationContainer.getIteration()));
					alphaNew = backtracking(estimationContainer.getAlpha(), gradient, b_DenominatorSum, Q);
					if (alphaNew == null) {
						return false;
					}
				} else {
					estimateInfo.add("reset" + estimationContainer.getID(), Integer.toString(estimationContainer.getIteration()));
					return false;
				}
				// update value
				estimationContainer.add(alphaNew, getLogLikelihood(alphaNew, nominalData));
			} else {
				// update value
				estimationContainer.add(alphaNew, getLogLikelihood(alphaNew, nominalData));

				// check if converged
				double delta = Math.abs(estimationContainer.getLogLikelihood() - loglikOld);
				if (delta <= minkaParameters.getEpsilon()) {
					converged = true;
				}
			}
		}

		return true;
	}
	
	private double[] getRowWiseSums(final NominalData dirMultData) {
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

	public void addStatResultInfo(final EstimationContainer estimationContainer, final ExtendedInfo info) {
		if (! estimationContainer.isNumericallyStable()) {
			info.add("NumericallyInstable", "true");
		}
	}
	
	public boolean estimate(final ConditionEstimate estimationContainer, final AbstractAlphaInit alphaInit, final ExtendedInfo info, final boolean backtrack) {
		// perform an initial guess of alpha
		final double[] initAlpha 	= alphaInit.init(estimationContainer.getNominalData());
		final double logLikelihood 	= getLogLikelihood(initAlpha, estimationContainer.getNominalData());
		estimationContainer.add(initAlpha, logLikelihood);
		
		boolean flag = false;
		try {
				// estimate alpha(s), capture and info(s), and store log-likelihood
				flag = maximizeLogLikelihood(estimationContainer, info, backtrack);
		} catch (StackOverflowError e) {
			// catch numerical instabilities and report
			estimationContainer.setNumericallyUnstable();
		}
		
		return flag;
	}
	
	public boolean estimate(final EstimationContainer estimationContainer, final ExtendedInfo info) {
		final Set<ConditionEstimate> successfullEstimates= new HashSet<ConditionEstimate>();
		
		for (final ConditionEstimate estimation : estimationContainer.getEstimates()) {
			try {
				if (estimation.previousEstimate() || estimate(estimation, minkaParameters.getAlphaInit(), info, false)) {
					successfullEstimates.add(estimation);
				} else {
					break;
				}
			} catch (StackOverflowError e) {
				// catch numerical instabilities and report
				estimation.setNumericallyUnstable();
			}
		}
		if (successfullEstimates.size() == estimationContainer.getEstimates().size()) {
			return true;
		}
		
		// estimate alpha(s), capture info(s), and store log-likelihood
		for (final ConditionEstimate estimate : estimationContainer.getEstimates()) {
			if (successfullEstimates.contains(estimate) || estimate.previousEstimate()) {
				estimate.clear();
				successfullEstimates.remove(estimate);
			}
			if (estimate(estimate, minkaParameters.getFallbackAlphaInit(), info, true)) {
				successfullEstimates.add(estimate);
			}
		}
		return successfullEstimates.size() == estimationContainer.getEstimates().size();
	}
	
	public void addAlphaValues(final EstimationContainer estimationContainer, final ExtendedInfo info) {
		for (final ConditionEstimate conditionEstimate : estimationContainer.getConditionEstimates()) {
			addAlphaValues(conditionEstimate, info);
		}
		addAlphaValues(estimationContainer.getPooledEstimate(), info);
	}
	
	private void addAlphaValues(final ConditionEstimate conditionEstimate, final ExtendedInfo info) {
		final String id 			= conditionEstimate.getID();
		final int iteration			= conditionEstimate.getIteration();
		final double[] initAlpha 	= conditionEstimate.getAlpha(0);
		final double[] alpha 		= conditionEstimate.getAlpha(iteration);
		final double logLikelihood	= conditionEstimate.getLogLikelihood(iteration);
		
		info.add("initAlpha" + id, Util.format(initAlpha[0]));			
		for (int i = 1; i < initAlpha.length; ++i) {
			info.add("initAlpha" + id, Util.format(initAlpha[i]));
		}
		info.add("alpha" + id, Util.format(alpha[0]));			
		for (int i = 1; i < alpha.length; ++i) {
			info.add("alpha" + id, Util.format(alpha[i]));
		}
		info.add("iteration" + id, Integer.toString(iteration));
		info.add("logLikelihood" + id, Double.toString(logLikelihood));
	}
	
	public double getScore(
			final EstimationContainer estimateContainer) {
		return sumLogLikeliood(estimateContainer.getConditionEstimates()) - estimateContainer.getPooledEstimate().getLogLikelihood();
	}
	
	public double getLRT(final EstimationContainer estimateContainer) {
		return - 2 * (
				estimateContainer.getPooledEstimate().getLogLikelihood() -
				sumLogLikeliood(estimateContainer.getConditionEstimates()));
	}
	
	public double sumLogLikeliood(final ConditionEstimate[] estimationContainers) {
		double logLikelihood = 0.0;
		for (final ConditionEstimate estimationContainer : estimationContainers) {
			logLikelihood += estimationContainer.getLogLikelihood();
		}
		
		return logLikelihood;
	}
	
}
