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
	 * Implementation of parameter estimation of a DirichletMultinomial based on
	 * Minka algorithm - estimate alpha and returns log-lik
	 * 
	 * @param
	 * @param estimateInfo
	 * @param backtrack
	 * @return
	 * 
	 *         Tested in test.lib.estimate.MinkaEstimateDirMultAlphaTest
	 */
	public boolean maximizeLogLikelihood(final ConditionEstimate conditionEstimate, final boolean backtrack) {
		final NominalData nominalData = conditionEstimate.getNominalData();
		final int categories = nominalData.getCategories();

		final double[] localSums = getRowWiseSums(nominalData);

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
		while (conditionEstimate.getNextIteration() < conditionEstimate.getMaxIterations() && !converged) {
			// init alpha new
			double[] alphaNew = new double[categories];
			Arrays.fill(alphaNew, 0.0);

			// pre-compute
			summedAlphaOld = MathUtil.sum(conditionEstimate.getAlpha());
			digammaSummedAlphaOld = Gamma.digamma(summedAlphaOld);
			trigammaSummedAlphaOld = Gamma.trigamma(summedAlphaOld);

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
					gradient[i] += Gamma
							.digamma(nominalData.getReplicate(replicateIndex)[i] + conditionEstimate.getAlpha()[i]);
					gradient[i] -= Gamma.digamma(conditionEstimate.getAlpha()[i]);

					// calculate Q
					Q[i] += Gamma
							.trigamma(nominalData.getReplicate(replicateIndex)[i] + conditionEstimate.getAlpha()[i]);
					Q[i] -= Gamma.trigamma(conditionEstimate.getAlpha()[i]);
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

			loglikOld = getLogLikelihood(conditionEstimate.getAlpha(), nominalData);

			// try update alphaNew
			boolean admissible = true;
			for (int i = 0; i < categories; ++i) {
				alphaNew[i] = conditionEstimate.getAlpha()[i] - (gradient[i] - b) / Q[i];

				if (alphaNew[i] < 0.0) {
					admissible = false;
				}
			}

			// check if alpha negative
			if (!admissible) {
				if (backtrack) {
					alphaNew = backtracking(conditionEstimate.getAlpha(), gradient, b_DenominatorSum, Q);
					if (alphaNew == null) {
						return false;
					}
				} else {
					conditionEstimate.addReset();
					return false;
				}
				// update value
				conditionEstimate.add(alphaNew, getLogLikelihood(alphaNew, nominalData));
			} else {
				// update value
				conditionEstimate.add(alphaNew, getLogLikelihood(alphaNew, nominalData));

				// check if converged
				double delta = Math.abs(conditionEstimate.getLogLikelihood() - loglikOld);
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
	 *         Tested in test.lib.estimate.MinkaEstimateDirMultAlphaTest
	 */
	public double getLogLikelihood(final double[] alpha, final NominalData dirMultData) {
		double logLikelihood = 0.0;
		final double alphaSum = MathUtil.sum(alpha);
		final double[] replicates = getRowWiseSums(dirMultData);

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

	protected double[] backtracking(final double[] alpha, final double[] gradient, final double b, final double[] Q) {
		double[] alphaNew = new double[alpha.length];

		// try smaller newton steps
		double lamba = 1.0;
		// decrease by
		double offset = 0.1;

		while (lamba >= 0.0) {
			lamba = lamba - offset;

			// adjust alpha with smaller newton step
			for (int i = 0; i < alpha.length; i++) {
				alphaNew[i] = alpha[i] - lamba * (gradient[i] - b) / Q[i];
				// check if admissible
				if (alphaNew[i] < 0.0) {
					return null;
				}
			}

			return alphaNew;
		}

		// could not find alpha(s)
		return null;
	}

	/*
	 * public void addStatResultInfo(final EstimationContainer estimationContainer,
	 * final ExtendedInfo info) { if (! estimationContainer.isNumericallyStable()) {
	 * info.add("NumericallyInstable", "true"); } }
	 */

	public boolean estimate(final ConditionEstimate estimationContainer, final AbstractAlphaInit alphaInit,
			final boolean backtrack) {
		// perform an initial guess of alpha
		final double[] initAlpha = alphaInit.init(estimationContainer.getNominalData());
		final double logLikelihood = getLogLikelihood(initAlpha, estimationContainer.getNominalData());
		estimationContainer.add(initAlpha, logLikelihood);

		boolean flag = false;
		try {
			// estimate alpha(s), capture and info(s), and store log-likelihood
			flag = maximizeLogLikelihood(estimationContainer, backtrack);
		} catch (StackOverflowError e) {
			// catch numerical instabilities and report
			estimationContainer.setNumericallyUnstable();
		}

		return flag;
	}

	public boolean estimate(final EstimationContainer estimationContainer) {
		final Set<ConditionEstimate> successfullEstimates = new HashSet<ConditionEstimate>();

		for (final ConditionEstimate estimation : estimationContainer.getEstimates()) {
			try {
				if (estimate(estimation, minkaParameters.getAlphaInit(), false)) {
					estimation.setSuccessfull();
					successfullEstimates.add(estimation);
				} else {
					estimation.setFailed();
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
		for (final ConditionEstimate estimation : estimationContainer.getEstimates()) {
			if (successfullEstimates.contains(estimation)) { // || estimate.previousEstimate()
				estimation.clear();
				successfullEstimates.remove(estimation);
			}
			if (estimate(estimation, minkaParameters.getFallbackAlphaInit(), true)) {
				successfullEstimates.add(estimation);
				estimation.setSuccessfull();
			} else {
				estimation.setFailed();
			}
		}
		return successfullEstimates.size() == estimationContainer.getEstimates().size();
	}

	public void addEstimationInfo(final EstimationContainer estimationContainer, final ExtendedInfo info,
			String prefix) {
		prefix += "alpha_estimation";
		for (final ConditionEstimate conditionEstimate : estimationContainer.getEstimates()) {
			final StringBuilder sb = new StringBuilder();
			if (conditionEstimate.getBacktracks().size() > 0) {
				sb.append("B:" + Util.join(conditionEstimate.getBacktracks(), ',') + ";");
			}
			if (conditionEstimate.getResets().size() > 0) {
				sb.append("R:" + Util.join(conditionEstimate.getResets(), ',') + ";");
			}
			String key = "E";
			if (conditionEstimate.successfull()) {
				key = "T";
			}
			if (conditionEstimate.failed()) {
				key = "F";
			}
			sb.append(key + ":" + conditionEstimate.getNextIteration());
			sb.append(">");
			if (estimationContainer.isNumericallyStable()) {
				sb.append("stable");
			} else {
				sb.append("instable");
			}

			info.append(prefix + conditionEstimate.getID(), sb.toString());
		}
	}

	public void addAlphaValues(final EstimationContainer estimationContainer, final ExtendedInfo info,
			final String prefix) {
		for (final ConditionEstimate conditionEstimate : estimationContainer.getEstimates()) {
			final String id = conditionEstimate.getID();
			final int iteration = conditionEstimate.getNextIteration() - 1;
			final double[] initAlpha = conditionEstimate.getAlpha(0);
			final double[] alpha = conditionEstimate.getAlpha(iteration);
			final double logLikelihood = conditionEstimate.getLogLikelihood(iteration);

			info.add(prefix + "init_alpha" + id, Util.format(initAlpha[0]));
			for (int i = 1; i < initAlpha.length; ++i) {
				info.append(prefix + "init_alpha" + id, Util.format(initAlpha[i]), ",");
			}
			info.add(prefix + "alpha" + id, Util.format(alpha[0]));
			for (int i = 1; i < alpha.length; ++i) {
				info.append(prefix + "alpha" + id, Util.format(alpha[i]), ",");
			}
			info.add(prefix + "log_likelihood" + id, Double.toString(logLikelihood));
		}
	}

	public double getScore(final EstimationContainer estimateContainer) {
		return sumLogLikeliood(estimateContainer.getConditionEstimates())
				- estimateContainer.getPooledEstimate().getLogLikelihood();
	}

	public double getLRT(final EstimationContainer estimateContainer) {
		return -2 * (estimateContainer.getPooledEstimate().getLogLikelihood()
				- sumLogLikeliood(estimateContainer.getConditionEstimates()));
	}

	public double sumLogLikeliood(final ConditionEstimate[] estimationContainers) {
		double logLikelihood = 0.0;
		for (final ConditionEstimate estimationContainer : estimationContainers) {
			logLikelihood += estimationContainer.getLogLikelihood();
		}

		return logLikelihood;
	}

}
