package lib.stat.betabin;

import java.util.List;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;

import lib.data.ParallelData;
import lib.data.result.MultiStatResult;
import lib.data.result.Result;
import lib.estimate.MinkaEstimateDirMultAlpha;
import lib.stat.AbstractStat;
import lib.stat.estimation.EstimationContainer;
import lib.stat.estimation.provider.arrest.LRTarrestEstimationCountProvider;
import lib.util.ExtendedInfo;
import lib.util.Util;

public class LRTarrestStat extends AbstractStat {

	private final LRTarrestEstimationCountProvider estimationContainerProvider;
	private final LRTarrestBetaBinParameter dirMultPrm;

	private final double threshold;
	private final MinkaEstimateDirMultAlpha estimateDirMultAlpha;
	private final ChiSquaredDistribution chiSquaredDistribution;
	
	public LRTarrestStat(
			final double threshold,
			final LRTarrestEstimationCountProvider estCountProv,
			final LRTarrestBetaBinParameter dirMultPrm) {

		this.threshold						= threshold;
		this.estimationContainerProvider 	= estCountProv;
		this.dirMultPrm 					= dirMultPrm;
		
		estimateDirMultAlpha	= new MinkaEstimateDirMultAlpha(dirMultPrm.getMinkaEstimateParameter());
		chiSquaredDistribution 	= new ChiSquaredDistribution(1);
	}

	// FIXME add valueIndex
	@Override
	protected void postProcess(final Result result, final int valueIndex) {
		if (dirMultPrm.showAlpha()) {
			estimateDirMultAlpha.addAlphaValues(result.getResultInfo(valueIndex));
		}
		estimateDirMultAlpha.addStatResultInfo(result.getResultInfo(valueIndex));
	}
	
	private double getPValue(final double lrt) {
		return 1 - chiSquaredDistribution.cumulativeProbability(lrt);
	} 
	
	@Override
	public Result calculate(ParallelData parallelData) {
		final EstimationContainer[] estContainers = estimationContainerProvider.convert(parallelData);
		final ExtendedInfo resultInfo = new ExtendedInfo(parallelData.getReplicates());
		final double lrt 	= estimateDirMultAlpha.getLRT(estContainers, resultInfo);
		final double pvalue = getPValue(lrt);
		
		final List<Integer> arrestPositions = parallelData.getCombPooledData()
				.getArrestPos2BCC().getPositions();
		final MultiStatResult multiStatResult = new MultiStatResult(parallelData);
		for (final int arrestPos : arrestPositions) {
			if (arrestPos == parallelData.getCoordinate().get1Position()) {
				final int newValueIndex = multiStatResult.addStat(pvalue);
				multiStatResult.getResultInfo(newValueIndex).addSite(RTarrestStat.ARREST_SCORE, Util.format(lrt));				
			} else {
				multiStatResult.addStat(Double.NaN);
			}
		}
		return multiStatResult;
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