package lib.stat.betabin;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;

import lib.data.ParallelData;
import lib.data.result.OneStatResult;
import lib.data.result.Result;
import lib.estimate.MinkaEstimateDirMultAlpha;
import lib.stat.AbstractStat;
import lib.stat.estimation.EstimationContainer;
import lib.stat.estimation.provider.arrest.AbstractRTarrestEstimationCountProvider;
import lib.util.ExtendedInfo;
import lib.util.Util;

public class RTarrestStat extends AbstractStat {

	public static final String ARREST_SCORE = "arrest_score";
	
	private final AbstractRTarrestEstimationCountProvider estimationContainerProvider;
	private final RTarrestBetaBinParameter dirMultPrarameter;

	private final double threshold;
	private final MinkaEstimateDirMultAlpha estimateDirMultAlpha;
	private final ChiSquaredDistribution dist;
	
	public RTarrestStat(
			final double threshold,
			final AbstractRTarrestEstimationCountProvider estimationContainerProvider,
			final RTarrestBetaBinParameter dirMultParameter) {

		this.threshold						= threshold;
		this.estimationContainerProvider 	= estimationContainerProvider;
		this.dirMultPrarameter 				= dirMultParameter;
		
		estimateDirMultAlpha	= new MinkaEstimateDirMultAlpha(dirMultParameter.getMinkaEstimateParameter());
		dist 	= new ChiSquaredDistribution(1);
	}

	@Override
	protected void postProcess(final Result result, final int valueIndex) {
		if (dirMultPrarameter.showAlpha()) {
			estimateDirMultAlpha.addAlphaValues(result.getResultInfo(valueIndex));
		}
		estimateDirMultAlpha.addStatResultInfo(result.getResultInfo(valueIndex));
	}
	
	private double getPValue(final double lrt) {
		return 1 - dist.cumulativeProbability(lrt);
	} 
	
	@Override
	public Result calculate(ParallelData parallelData) {
		final EstimationContainer[] estimationContainers = estimationContainerProvider.convert(parallelData);
		final ExtendedInfo resultInfo = new ExtendedInfo(parallelData.getReplicates()); 
		final double lrt 	= estimateDirMultAlpha.getLRT(estimationContainers, resultInfo);
		final double pvalue = getPValue(lrt);
		final Result result = new OneStatResult(pvalue, parallelData, resultInfo);
		result.getResultInfo().addSite(ARREST_SCORE, Util.format(lrt));
		return result;
	}
	
	@Override
	public boolean filter(final Result statResult) {
		final double statValue = statResult.getScore();

		if (Double.isNaN(threshold)) {
			return false;
		}

		return threshold > statValue;
	}
	
}