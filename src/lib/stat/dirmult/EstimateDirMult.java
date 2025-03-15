package lib.stat.dirmult;

import lib.estimate.MinkaEstimateDirMultAlpha;
import lib.estimate.MinkaParameter;
import lib.stat.estimation.EstimationContainer;
import lib.stat.initalpha.AbstractAlphaInit;
import lib.util.ExtendedInfo;
import lib.util.Util;

/*
 * TODO provider estimate values
 */

public class EstimateDirMult {

	private final MinkaParameter minkaPrm;
	private final MinkaEstimateDirMultAlpha minkaEstAlpha;
	
	private EstimationContainer[] estimationContainers;	

	public EstimateDirMult(final MinkaParameter minkaPrm) {
		minkaEstAlpha	= new MinkaEstimateDirMultAlpha(minkaPrm);
		this.minkaPrm 	= minkaPrm;
	}

	// TODO remove
	public void addStatResultInfo(final ExtendedInfo info) {
		if (! isNumericallyStable(estimationContainers)) {
			info.addSite("NumericallyInstable");
		}
	}
	
	public boolean isNumericallyStable(final EstimationContainer[] estContainers) {
		for (final EstimationContainer estContainer : estContainers) {
			if (! estContainer.isNumericallyStable()) {
				return false;
			}
		}

		return true;
	}
	
	public boolean estimate(final EstimationContainer estimationContainer, final AbstractAlphaInit alphaInit, final ExtendedInfo info, final boolean backtrack) {
		// perform an initial guess of alpha
		final double[] initAlpha 	= alphaInit.init(estimationContainer.getNominalData());
		final double logLikelihood 	= minkaEstAlpha.getLogLikelihood(initAlpha, estimationContainer.getNominalData());
		estimationContainer.add(initAlpha, logLikelihood);

		// estimate alpha(s), capture and info(s), and store log-likelihood
		return minkaEstAlpha.maximizeLogLikelihood(estimationContainer, info, backtrack);
	}
	
	private boolean estimate(final AbstractAlphaInit alphaInit, final ExtendedInfo info, boolean backtrack) {
		boolean flag = true;
		// estimate alpha(s), capture info(s), and store log-likelihood
		for (final EstimationContainer estimationContainer : estimationContainers) {
			try {
				flag &= estimate(estimationContainer, alphaInit, info, backtrack);
			} catch (StackOverflowError e) {
				// catch numerical instabilities and report
				estimationContainer.setNumericallyUnstable();
			}
		}
		return flag;
	}
	
	public double getScore(final EstimationContainer[] estimationContainers, final ExtendedInfo info) {
		estimate(estimationContainers, info);
		return getObservedlogLikeliood() - getPooledEstimationContainers().getLogLikelihood();
	}

	private void estimate(final EstimationContainer[] estimationContainers, final ExtendedInfo info) { 
		this.estimationContainers 					= estimationContainers;
	
		final AbstractAlphaInit defaultAlphaInit 	= minkaPrm.getAlphaInit();
		final AbstractAlphaInit fallbackAlphaInit 	= minkaPrm.getFallbackAlphaInit();
	
		if (! estimate(defaultAlphaInit, info, false)) {
			for (final EstimationContainer estimationContainer : estimationContainers) {
				estimationContainer.clear();
			}
			estimate(fallbackAlphaInit, info, true);
		}
	}
	
	private double getObservedlogLikeliood() {
		double tmpLogLikelihood = 0.0;
		final int conditions = estimationContainers.length - 1;
		for (int conditionIndex = 0; conditionIndex < conditions; conditionIndex++) {
			tmpLogLikelihood += getEstimationContainer(conditionIndex).getLogLikelihood();
		}
		return tmpLogLikelihood;
	}
	
	public double getLRT(final EstimationContainer[] estimationContainers, final ExtendedInfo info) {
		estimate(estimationContainers, info);
		return - 2 * (getPooledEstimationContainers().getLogLikelihood() - getObservedlogLikeliood());
	}
	
	private  EstimationContainer getEstimationContainer(final int conditionIndex) {
		return estimationContainers[conditionIndex];
	}
	
	private  EstimationContainer getPooledEstimationContainers() {
		return estimationContainers[estimationContainers.length - 1];
	}

	public void addShowAlpha(final ExtendedInfo info) {
		for (final EstimationContainer estimationContainer : estimationContainers) {
			final String id 			= estimationContainer.getID();
			final int iteration			= estimationContainer.getIteration();
			final double[] initAlpha 	= estimationContainer.getAlpha(0);
			final double[] alpha 		= estimationContainer.getAlpha(iteration);
			final double logLikelihood	= estimationContainer.getLogLikelihood(iteration);
			
			info.addSite("initAlpha" + id, Util.format(initAlpha[0]));			
			for (int i = 1; i < initAlpha.length; ++i) {
				info.addSite("initAlpha" + id, ":");
				info.addSite("initAlpha" + id, Util.format(initAlpha[i]));
			}
			
			info.addSite("alpha" + id, Util.format(alpha[0]));			
			for (int i = 1; i < alpha.length; ++i) {
				info.addSite("alpha" + id, ":");
				info.addSite("alpha" + id, Util.format(alpha[i]));
			}
		
			info.addSite("iteration" + id, Integer.toString(iteration));
			info.addSite("logLikelihood" + id, Double.toString(logLikelihood));
		}
	}
	
}