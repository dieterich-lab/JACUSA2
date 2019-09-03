package lib.stat.dirmult;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;

import lib.data.ParallelData;
import lib.data.result.OneStatResult;
import lib.data.result.Result;
import lib.stat.AbstractStat;
import lib.stat.estimation.EstimationContainer;
import lib.stat.estimation.provider.EstimationContainerProvider;

public class CallStat extends AbstractStat {

	private final double threshold;
	private final EstimationContainerProvider estimationSampleProvider;
	private final DirMultParameter dirMultPrm;

	private final EstimateDirMult dirMult;
	
	public CallStat(
			final double threshold,
			final EstimationContainerProvider estimationSampleProvider,
			final DirMultParameter dirMultPrm) {

		this.threshold 				= threshold;
		this.estimationSampleProvider 	= estimationSampleProvider;
		this.dirMultPrm 			= dirMultPrm;

		dirMult						= new EstimateDirMult(dirMultPrm.getMinkaEstimateParameter()); 
	}

	@Override
	public void addStatResultInfo(final Result statResult) {
		if (dirMultPrm.isShowAlpha()) {
			dirMult.addShowAlpha();
		}
		dirMult.addStatResultInfo(statResult.getResultInfo());
	}
	
	@Override
	public Result calculate(ParallelData parallelData) {
		final EstimationContainer[] estimationSamples = estimationSampleProvider.convert(parallelData);
		double stat = Double.NaN;
		if (dirMultPrm.isCalcPValue()) {
			stat = dirMult.getLRT(estimationSamples);
			// TODO degrees of freedom
			final ChiSquaredDistribution dist = new ChiSquaredDistribution(1); 
			stat = 1 - dist.cumulativeProbability(stat);
		} else {
			stat = dirMult.getScore(estimationSamples);;
		}
		return new OneStatResult(stat, parallelData);
	}
	
	@Override
	public boolean filter(final Result statResult) {
		final double statValue = statResult.getStat();

		if (Double.isNaN(threshold)) {
			return false;
		}

		// if p-value interpret threshold as upper bound
		if (dirMultPrm.isCalcPValue()) {
			return threshold < statValue;
		}

		// if log-likelihood ratio interpret threshold as lower bound
		return statValue < threshold;
	}
	
}