package lib.stat.betabin;

import java.util.List;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;

import lib.data.ParallelData;
import lib.data.result.MultiStatResult;
import lib.data.result.OneStatResult;
import lib.data.result.Result;
import lib.stat.AbstractStat;
import lib.stat.dirmult.EstimateDirMult;
import lib.stat.sample.EstimationSample;
import lib.stat.sample.provider.arrest.LRTarrestCountSampleProvider;
import lib.util.Util;

public class LRTarrestStat extends AbstractStat {

	private final LRTarrestCountSampleProvider estimationSampleProvider;
	private final LRTarrestBetaBinParameter dirMultParameter;

	private final double threshold;
	private final EstimateDirMult dirMult;
	
	private final ChiSquaredDistribution dist;
	
	public LRTarrestStat(
			final double threshold,
			final LRTarrestCountSampleProvider estimationSampleProvider,
			final LRTarrestBetaBinParameter dirMultParameter) {

		this.threshold					= threshold;
		this.estimationSampleProvider 	= estimationSampleProvider;
		this.dirMultParameter 			= dirMultParameter;
		
		dirMult							= new EstimateDirMult(dirMultParameter.getMinkaEstimateParameter());
		dist = new ChiSquaredDistribution(1);
	}

	@Override
	public void addStatResultInfo(final Result statResult) {
		if (dirMultParameter.isShowAlpha()) {
			dirMult.addShowAlpha();
		}
		dirMult.addStatResultInfo(statResult.getResultInfo());
	}
	
	private double getPValue(final double lrt) {
		return 1 - dist.cumulativeProbability(lrt);
	} 
	
	@Override
	public Result calculate(ParallelData parallelData) {
		final EstimationSample[] estimationSamples = estimationSampleProvider.convert(parallelData);
		final double lrt 	= dirMult.getLRT(estimationSamples);
		final double pvalue = getPValue(lrt);
		final Result oneStatResult = new OneStatResult(pvalue, parallelData);
		oneStatResult.getResultInfo().add(RTarrestStat.ARREST_SCORE, Util.format(lrt));
		
		final List<Integer> arrestPositions = parallelData.getCombinedPooledData()
				.getArrestPos2BaseCallCount().getPositions();
		MultiStatResult multiStatResult = new MultiStatResult(oneStatResult);
		for (int i = 0; i < arrestPositions.size(); ++i) {
			multiStatResult.addStat(Double.NaN);
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