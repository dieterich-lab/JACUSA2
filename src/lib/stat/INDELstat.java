package lib.stat;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;

import lib.data.ParallelData;
import lib.data.result.OneStatResult;
import lib.data.result.Result;
import lib.estimate.MinkaEstimateDirMultAlpha;
import lib.stat.dirmult.EstimationParameter;
import lib.stat.estimation.EstimationContainer;
import lib.stat.estimation.provider.INDELestimateProvider;
import lib.util.ExtendedInfo;
import lib.util.Util;

public class INDELstat extends AbstractStat {

	private final EstimationParameter estimationParameter;
	private final INDELestimateProvider estimationContainerProvider;
	private final MinkaEstimateDirMultAlpha minka;
	private final ChiSquaredDistribution chiSquaredDistribution;
	
	private final String prefix;

	private EstimationContainer estimationContainer;
	
	public INDELstat(
			final EstimationParameter estimationParameter,
			final INDELestimateProvider indelEstimateProvider,
			final String prefix) {
		super();

		this.estimationParameter			= estimationParameter;
		this.estimationContainerProvider 	= indelEstimateProvider;
		this.minka							= new MinkaEstimateDirMultAlpha(estimationParameter.getMinkaParameter());
		this.chiSquaredDistribution			= new ChiSquaredDistribution(1);
		
		this.prefix	= prefix;
	}
	
	public EstimationContainer getEstimationContainer() {
		return estimationContainer;
	}

	public MinkaEstimateDirMultAlpha getMinka() {
		return minka;
	}
	
	public INDELestimateProvider getEstimationContainerProvider() {
		return estimationContainerProvider;
	}
	
	@Override
	public Result process(ParallelData parallelData, ExtendedInfo resultInfo) {
		estimationContainer = estimationContainerProvider.convert(parallelData);
		minka.estimate(estimationContainer);
		minka.addEstimationInfo(estimationContainer, resultInfo, prefix);
		
		/* TODO remove
		for (final ConditionEstimate conditionEstimate : estimationContainer.getConditionEstimates()) {
			resultInfo.add(
					prefix + "_numerically_instable" + conditionEstimate.getID(),
					Boolean.toString(!estimationContainer.getConditionEstimate(0).isNumericallyStable()));
		}
		*/
		
		if (estimationParameter.showAlpha()) {
			minka.addAlphaValues(estimationContainer, resultInfo, prefix);
		}
		
		final double lrt 	= minka.getLRT(estimationContainer);
		final double pvalue = getPValue(lrt);
	
		resultInfo.add(prefix + "score", Util.format(lrt));
		resultInfo.add(prefix + "pvalue", Util.format(pvalue));

		final Result result = new OneStatResult(lrt, parallelData, resultInfo);
		
		
		return result;
	}
	
	public double getPValue(final double lrt) {
		return 1 - chiSquaredDistribution.cumulativeProbability(lrt);
	}
	
	public String getPrefix() {
		return prefix;
	}
	
}
