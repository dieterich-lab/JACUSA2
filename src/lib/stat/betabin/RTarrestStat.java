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
		
		estimateDirMultAlpha	= new MinkaEstimateDirMultAlpha(dirMultParameter.getMinkaParameter());
		dist 	= new ChiSquaredDistribution(1);
	}
	
	private double getPValue(final double lrt) {
		return 1 - dist.cumulativeProbability(lrt);
	} 
	
	@Override
	public Result process(ParallelData parallelData, ExtendedInfo info) {
		final EstimationContainer estimationContainer = estimationContainerProvider.convert(parallelData);
		final double lrt 	= estimateDirMultAlpha.getLRT(estimationContainer);
		final double pvalue = getPValue(lrt);
		if (! filter(pvalue)) {
			return null;
		}
		
		final ExtendedInfo resultInfo = new ExtendedInfo();
		resultInfo.add(ARREST_SCORE, Util.format(lrt));
		
		if (dirMultPrarameter.showAlpha()) {
			estimateDirMultAlpha.addAlphaValues(estimationContainer, resultInfo, "");
		}
		estimateDirMultAlpha.addStatResultInfo(estimationContainer, resultInfo);
		
		final Result result = new OneStatResult(pvalue, parallelData, resultInfo);

		return result;
	}
	
	public boolean filter(final double pvalue) {
		if (Double.isNaN(threshold)) {
			return false;
		}

		return threshold > pvalue;
	}
	
}