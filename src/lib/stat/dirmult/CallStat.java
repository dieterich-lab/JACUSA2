package lib.stat.dirmult;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;

import lib.data.ParallelData;
import lib.data.result.OneStatResult;
import lib.data.result.Result;
import lib.estimate.MinkaEstimateDirMultAlpha;
import lib.stat.AbstractStat;
import lib.stat.estimation.EstimationContainer;
import lib.stat.estimation.provider.ConditionEstimateProvider;
import lib.util.ExtendedInfo;

public class CallStat extends AbstractStat {

	private final double threshold;
	private final ConditionEstimateProvider estimationContainerProvider;
	private final EstimationParameter dirMultParameter;
	private final MinkaEstimateDirMultAlpha estimateDirMultAlpha;

	private EstimationContainer estimationContainer;
	
	public CallStat(
			final double threshold,
			final ConditionEstimateProvider estimationContainerProvider,
			final EstimationParameter dirMultParameter) {
		this.threshold 						= threshold;
		this.estimationContainerProvider 	= estimationContainerProvider;
		this.dirMultParameter 				= dirMultParameter;
		estimateDirMultAlpha 				= new MinkaEstimateDirMultAlpha(dirMultParameter.getMinkaParameter());
	}

	public EstimationContainer getEstimationContainer() {
		return estimationContainer;
	}

	public EstimationParameter getDirMultParameter() {
		return dirMultParameter;
	}
	
	public MinkaEstimateDirMultAlpha getMinka() {
		return estimateDirMultAlpha;
	}
	
	public double getStat(final EstimationContainer estimationContainer) {
		double stat;
		
		if (dirMultParameter.calcPValue()) {
			stat = estimateDirMultAlpha.getLRT(estimationContainer);
			// TODO degrees of freedom
			final ChiSquaredDistribution dist = new ChiSquaredDistribution(3);
			stat = 1 - dist.cumulativeProbability(stat);
		} else {
			stat = estimateDirMultAlpha.getScore(estimationContainer);
		}
		
		return stat;
	}
	
	@Override
	public Result process(ParallelData parallelData, ExtendedInfo resultInfo) {
		estimationContainer = estimationContainerProvider.convert(parallelData);
		final boolean estimationSuccesfull = estimateDirMultAlpha.estimate(estimationContainer);
		
		double stat = estimateDirMultAlpha.getScore(estimationContainer);
		if (filter(stat)) {
			return null;
		}
		
		if (!estimationSuccesfull) {
			resultInfo.add("score_estimation", "0");
		} else {
			resultInfo.add("score_estimation", "1");
		}
		estimateDirMultAlpha.addEstimationInfo(estimationContainer, resultInfo, "");
		if (dirMultParameter.showAlpha()) {
			estimateDirMultAlpha.addAlphaValues(estimationContainer, resultInfo, "");
		}
		estimateDirMultAlpha.addStatResultInfo(estimationContainer, resultInfo);
		
		return new OneStatResult(stat, parallelData, resultInfo);
	}
	
	public ConditionEstimateProvider getConditionEstimateProvider() {
		return estimationContainerProvider;
	}
	
	public boolean filter(final double statValue) {
		if (Double.isNaN(threshold)) {
			return false;
		}

		// if p-value interpret threshold as upper bound
		if (dirMultParameter.calcPValue()) {
			return threshold < statValue;
		}

		// if log-likelihood ratio interpret threshold as lower bound
		return statValue < threshold;
	}
	
}