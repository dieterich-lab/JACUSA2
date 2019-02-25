package lib.stat.betabin;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;

import lib.data.ParallelData;
import lib.data.result.OneStatResult;
import lib.data.result.Result;
import lib.stat.AbstractStat;
import lib.stat.dirmult.EstimateDirMult;
import lib.stat.sample.EstimationSample;
import lib.stat.sample.provider.arrest.LRTarrestCountSampleProvider;

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
	
	private double getPValue(final EstimationSample[] estimationSamples) {
		final double lrt = dirMult.getLRT(estimationSamples);
		return 1 - dist.cumulativeProbability(lrt);
	}
	
	@Override
	public Result calculate(ParallelData parallelData) {
		final EstimationSample[] estimationSamples = estimationSampleProvider.convert(parallelData);
		final double pvalue = getPValue(estimationSamples);
		return new OneStatResult(pvalue, parallelData);
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