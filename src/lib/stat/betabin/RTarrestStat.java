package lib.stat.betabin;

import lib.data.ParallelData;
import lib.data.result.OneStatResult;
import lib.data.result.Result;
import lib.stat.AbstractStat;
import lib.stat.dirmult.EstimateDirMult;
import lib.stat.sample.EstimationSample;
import lib.stat.sample.provider.arrest.RTarrestCountSampleProvider;

public class RTarrestStat extends AbstractStat {

	private final RTarrestCountSampleProvider estimationSampleProvider;
	private final RTarrestBetaBinParameter dirMultParameter;

	private final EstimateDirMult dirMult;
	
	public RTarrestStat(
			final RTarrestCountSampleProvider estimationSampleProvider,
			final RTarrestBetaBinParameter dirMultParameter) {

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
		final double stat = dirMult.getStatistic(estimationSamples);
		return new OneStatResult(stat, parallelData);
	}
	
	@Override
	public boolean filter(final Result statResult) {
		final double statValue = statResult.getStat();

		if (Double.isNaN(dirMultParameter.getThreshold())) {
			return false;
		}

		return dirMultParameter.getThreshold() < statValue;
	}
	
}