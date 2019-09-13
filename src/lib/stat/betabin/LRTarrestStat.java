package lib.stat.betabin;

import java.util.List;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;

import lib.data.ParallelData;
import lib.data.result.MultiStatResult;
import lib.data.result.Result;
import lib.stat.AbstractStat;
import lib.stat.dirmult.EstimateDirMult;
import lib.stat.estimation.EstimationContainer;
import lib.stat.estimation.provider.arrest.LRTarrestEstimationCountProvider;
import lib.util.Util;

public class LRTarrestStat extends AbstractStat {

	private final LRTarrestEstimationCountProvider estContainerProv;
	private final LRTarrestBetaBinParameter dirMultPrm;

	private final double threshold;
	private final EstimateDirMult dirMult;
	
	private final ChiSquaredDistribution dist;
	
	public LRTarrestStat(
			final double threshold,
			final LRTarrestEstimationCountProvider estCountProv,
			final LRTarrestBetaBinParameter dirMultPrm) {

		this.threshold		= threshold;
		this.estContainerProv 	= estCountProv;
		this.dirMultPrm 	= dirMultPrm;
		
		dirMult	= new EstimateDirMult(dirMultPrm.getMinkaEstimateParameter());
		dist 	= new ChiSquaredDistribution(1);
	}

	@Override
	public void addStatResultInfo(final Result statResult) {
		if (dirMultPrm.isShowAlpha()) {
			dirMult.addShowAlpha();
		}
		dirMult.addStatResultInfo(statResult.getResultInfo());
	}
	
	private double getPValue(final double lrt) {
		return 1 - dist.cumulativeProbability(lrt);
	} 
	
	@Override
	public Result calculate(ParallelData parallelData) {
		final EstimationContainer[] estContainers = estContainerProv.convert(parallelData);
		final double lrt 	= dirMult.getLRT(estContainers);
		final double pvalue = getPValue(lrt);
		
		final List<Integer> arrestPositions = parallelData.getCombPooledData()
				.getArrestPos2BCC().getPositions();
		final MultiStatResult multiStatResult = new MultiStatResult(parallelData);
		for (final int arrestPos : arrestPositions) {
			if (arrestPos == parallelData.getCoordinate().get1Position()) {
				final int newValueIndex = multiStatResult.addStat(pvalue);
				multiStatResult.getResultInfo(newValueIndex).add(RTarrestStat.ARREST_SCORE, Util.format(lrt));				
			} else {
				multiStatResult.addStat(Double.NaN);
			}
		}
		return multiStatResult;
	}
	
	@Override
	public boolean filter(final Result statResult) {
		final double statValue = statResult.getStat();

		if (Double.isNaN(threshold)) {
			return false;
		}

		return threshold > statValue;
	}
	
}