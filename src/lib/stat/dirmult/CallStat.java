package lib.stat.dirmult;

import lib.data.ParallelData;
import lib.data.result.OneStatResult;
import lib.data.result.Result;
import lib.stat.AbstractStat;
import lib.stat.sample.EstimationSample;
import lib.stat.sample.provider.EstimationSampleProvider;

public class CallStat extends AbstractStat {

	private final double threshold;
	private final EstimationSampleProvider estimationSampleProvider;
	private final DirMultParameter dirMultParameter;

	private final EstimateDirMult dirMult;
	
	public CallStat(
			final double threshold,
			final EstimationSampleProvider estimationSampleProvider,
			final DirMultParameter dirMultParameter) {

		this.threshold 					= threshold;
		this.estimationSampleProvider 	= estimationSampleProvider;
		this.dirMultParameter 			= dirMultParameter;

		dirMult							= new EstimateDirMult(dirMultParameter.getMinkaEstimateParameter()); 
	}

	@Override
	public void addStatResultInfo(final Result statResult) {
		if (dirMultParameter.isShowAlpha()) {
			dirMult.addShowAlpha();
		}
		dirMult.addStatResultInfo(statResult.getResultInfo());
	}
	
	@Override
	public Result calculate(ParallelData parallelData) {
		final EstimationSample[] estimationSamples = estimationSampleProvider.convert(parallelData);
		final double stat = dirMult.getScore(estimationSamples);
		return new OneStatResult(stat, parallelData);
	}
	
	@Override
	public boolean filter(final Result statResult) {
		final double statValue = statResult.getStat();

		if (Double.isNaN(threshold)) {
			return false;
		}

		// if p-value interpret threshold as upper bound
		if (dirMultParameter.isCalcPValue()) {
			return threshold < statValue;
		}

		// if log-likelihood ratio interpret threshold as lower bound
		return statValue < threshold;
	}
	
}