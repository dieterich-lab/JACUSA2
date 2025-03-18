package lib.stat.dirmult;

import lib.stat.estimation.EstimationContainer;

public class DirichletMultinomial {

	public double getScore(
			final EstimationContainer[] conditionEstimationContainers,
			final EstimationContainer pooledEstinatiomContainer) {
		return sumLogLikeliood(conditionEstimationContainers) - pooledEstinatiomContainer.getLogLikelihood();
	}
	
	public double getLRT(
			final EstimationContainer[] conditionEstimationContainers, 
			final EstimationContainer pooledEstinatiomContainer) {
		return - 2 * (pooledEstinatiomContainer.getLogLikelihood() - sumLogLikeliood(conditionEstimationContainers));
	}
	
	public double sumLogLikeliood(final EstimationContainer[] estimationContainers) {
		double logLikelihood = 0.0;
		for (final EstimationContainer estimationContainer : estimationContainers) {
			logLikelihood += estimationContainer.getLogLikelihood();
		}
		
		return logLikelihood;
	}
	
}
