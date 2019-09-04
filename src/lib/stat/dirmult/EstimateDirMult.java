package lib.stat.dirmult;

import lib.estimate.MinkaEstimateDirMultAlpha;
import lib.estimate.MinkaParameter;
import lib.stat.estimation.EstimationContainer;
import lib.stat.initalpha.AbstractAlphaInit;
import lib.util.Info;
import lib.util.Util;

public class EstimateDirMult {

	private final MinkaParameter minkaPrm;
	private final MinkaEstimateDirMultAlpha minkaEstAlpha;
	
	private EstimationContainer[] estContainers;	
	private Info estimateInfo;

	public EstimateDirMult(final MinkaParameter minkaPrm) {
		minkaEstAlpha	= new MinkaEstimateDirMultAlpha(minkaPrm);
		this.minkaPrm 	= minkaPrm;
	}

	public void addStatResultInfo(final Info info) {
		if (! isNumericallyStable(estContainers)) {
			info.add("NumericallyInstable");
		}
		if (! estimateInfo.isEmpty()) {
			info.addAll(estimateInfo);
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
	
	public boolean estimate(final EstimationContainer estContainer, final AbstractAlphaInit alphaInit, final boolean backtrack) {
		// perform an initial guess of alpha
		final double[] initAlpha 	= alphaInit.init(estContainer.getNominalData());
		final double logLikelihood 	= minkaEstAlpha.getLogLikelihood(initAlpha, estContainer.getNominalData());
		estContainer.add(initAlpha, logLikelihood);

		// estimate alpha(s), capture and info(s), and store log-likelihood
		return minkaEstAlpha.maximizeLogLikelihood(estContainer, estimateInfo, backtrack);
	}
	
	private boolean estimate(final AbstractAlphaInit alphaInit, final boolean backtrack) {
		boolean flag = true;
		// estimate alpha(s), capture info(s), and store log-likelihood
		for (final EstimationContainer estContainer : estContainers) {
			try {
				flag &= estimate(estContainer, alphaInit, backtrack);
			} catch (StackOverflowError e) {
				// catch numerical instabilities and report
				estContainer.setNumericallyUnstable();
			}
		}
		return flag;
	}
	
	public double getScore(final EstimationContainer[] estContainers) {
		estimate(estContainers);
		return getObservedlogLikeliood() - getPooledEstContainers().getLogLikelihood();
	}

	private void estimate(final EstimationContainer[] estContainers) { 
		this.estContainers 	= estContainers;
		estimateInfo 		= new Info();
	
		final AbstractAlphaInit defaultAlphaInit 	= minkaPrm.getAlphaInit();
		final AbstractAlphaInit fallbackAlphaInit 	= minkaPrm.getFallbackAlphaInit();
	
		if (! estimate(defaultAlphaInit, false)) {
			for (final EstimationContainer estContainer : estContainers) {
				estContainer.clear();
			}
			estimate(fallbackAlphaInit, true);
		}
	}
	
	private double getObservedlogLikeliood() {
		double tmpLogLikelihood = 0.0;
		final int conditions = estContainers.length - 1;
		for (int condI = 0; condI < conditions; condI++) {
			tmpLogLikelihood += getEstContainer(condI).getLogLikelihood();
		}
		return tmpLogLikelihood;
	}
	
	public double getLRT(final EstimationContainer[] estContainers) {
		estimate(estContainers);
		return - 2 * (getPooledEstContainers().getLogLikelihood() - 
				getObservedlogLikeliood());
	}
	
	private  EstimationContainer getEstContainer(final int condI) {
		return estContainers[condI];
	}
	
	private  EstimationContainer getPooledEstContainers() {
		return estContainers[estContainers.length - 1];
	}

	public void addShowAlpha() {
		for (final EstimationContainer estContainer : estContainers) {
			final String id 			= estContainer.getId();
			final int iteration			= estContainer.getIteration();
			final double[] initAlpha 	= estContainer.getAlpha(0);
			final double[] alpha 		= estContainer.getAlpha(iteration);
			final double logLikelihood	= estContainer.getLogLikelihood(iteration);
			
			estimateInfo.add("initAlpha" + id, Util.format(initAlpha[0]));			
			for (int i = 1; i < initAlpha.length; ++i) {
				estimateInfo.add("initAlpha" + id, ":");
				estimateInfo.add("initAlpha" + id, Util.format(initAlpha[i]));
			}
			
			estimateInfo.add("alpha" + id, Util.format(alpha[0]));			
			for (int i = 1; i < alpha.length; ++i) {
				estimateInfo.add("alpha" + id, ":");
				estimateInfo.add("alpha" + id, Util.format(alpha[i]));
			}
		
			estimateInfo.add("iteration" + id, Integer.toString(iteration));
			estimateInfo.add("logLikelihood" + id, Double.toString(logLikelihood));
		}
	}
	
}