package lib.stat;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;

import lib.data.ParallelData;
import lib.data.result.OneStatResult;
import lib.data.result.Result;
import lib.estimate.MinkaEstimateDirMultAlpha;
import lib.estimate.MinkaParameter;
import lib.stat.estimation.EstimationContainer;
import lib.stat.estimation.provider.INDELestimateProvider;
import lib.util.ExtendedInfo;
import lib.util.Util;

public class INDELstat extends AbstractStat {

	private final INDELestimateProvider estimationContainerProvider;
	private final MinkaEstimateDirMultAlpha estimateDirMultAlpha;
	private final ChiSquaredDistribution chiSquaredDistribution;
	
	private final String scoreKey;
	private final String pvalueKey;

	private EstimationContainer estimationContainer;
	
	public INDELstat(
			final MinkaParameter minkaParameter,
			final INDELestimateProvider indelEstimateProvider,
			final String scoreKey,
			final String pvalueKey) {
		super();

		this.estimationContainerProvider 	= indelEstimateProvider;
		this.estimateDirMultAlpha			= new MinkaEstimateDirMultAlpha(minkaParameter);
		this.chiSquaredDistribution			= new ChiSquaredDistribution(1);
		
		this.scoreKey	= scoreKey;
		this.pvalueKey	= pvalueKey;
	}
	
	public EstimationContainer getEstimationContainer() {
		return estimationContainer;
	}

	public MinkaEstimateDirMultAlpha getMinka() {
		return estimateDirMultAlpha;
	}
	
	public double getLRT(final EstimationContainer estimationContainer) {
		return estimateDirMultAlpha.getLRT(estimationContainer);
	}
	
	@Override
	public Result process(ParallelData parallelData, ExtendedInfo resultInfo) {
		estimationContainer = estimationContainerProvider.convert(parallelData);
		final boolean successfullEstimation = estimateDirMultAlpha.estimate(estimationContainer, resultInfo);
		if (!successfullEstimation) {
			// TODO what should happen here?
		}

		final double lrt 	= estimateDirMultAlpha.getLRT(estimationContainer);
		final double pvalue = getPValue(lrt);
	
		resultInfo.add(scoreKey, Util.format(lrt));
		resultInfo.add(pvalueKey, Util.format(pvalue));

		final Result result = new OneStatResult(lrt, parallelData, resultInfo);

		return result;
	}
	
	public double getPValue(final double lrt) {
		return 1 - chiSquaredDistribution.cumulativeProbability(lrt);
	}
	
	public String getScoreKey() {
		return scoreKey;
	}
	
}
