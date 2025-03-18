package lib.stat;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;

import lib.data.ParallelData;
import lib.data.result.OneStatResult;
import lib.data.result.Result;
import lib.estimate.MinkaEstimateDirMultAlpha;
import lib.estimate.MinkaParameter;
import lib.stat.estimation.EstimationContainer;
import lib.stat.estimation.provider.INDELestimationCountProvider;
import lib.util.ExtendedInfo;
import lib.util.Util;

public class GenericStat extends AbstractStat {

	private final INDELestimationCountProvider estimationContainerProvider;
	private final MinkaEstimateDirMultAlpha estimateDirMultAlpha;
	private final ChiSquaredDistribution chiSquaredDistribution;
	
	private final String scoreKey;
	private final String pvalueKey;
	
	public GenericStat(
			final MinkaParameter minkaParameter,
			final INDELestimationCountProvider countSampleProvider,
			final String scoreKey,
			final String pvalueKey) {
		super();

		this.estimationContainerProvider 	= countSampleProvider;
		this.estimateDirMultAlpha			= new MinkaEstimateDirMultAlpha(minkaParameter);
		this.chiSquaredDistribution			= new ChiSquaredDistribution(1);
		
		this.scoreKey	= scoreKey;
		this.pvalueKey	= pvalueKey;
	}

	@Override
	protected boolean filter(Result statResult) {
		return false;
	}
	
	@Override
	public Result calculate(ParallelData parallelData) {
		final EstimationContainer[] estimationContainers = estimationContainerProvider.convert(parallelData);
		final ExtendedInfo resultInfo = new ExtendedInfo(parallelData.getReplicates());
		final double lrt 	= estimateDirMultAlpha.getLRT(estimationContainers, resultInfo);
		final Result result = new OneStatResult(lrt, parallelData, resultInfo);

		return result;
	}

	@Override
	protected void postProcess(final Result result, final int valueIndex) {
		final double lrt = result.getScore(valueIndex);
		final double pvalue = 1 - chiSquaredDistribution.cumulativeProbability(result.getScore());
	
		final ExtendedInfo resultInfo = result.getResultInfo(valueIndex);
		resultInfo.addSite(scoreKey, Util.format(lrt));
		resultInfo.addSite(pvalueKey, Util.format(pvalue));
	}
		
	public String getScoreKey() {
		return scoreKey;
	}
	
}
