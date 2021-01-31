package lib.stat.dirmult;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;

import lib.data.ParallelData;
import lib.data.result.OneStatResult;
import lib.data.result.Result;
import lib.stat.AbstractStat;
import lib.stat.estimation.EstimationContainer;
import lib.stat.estimation.provider.EstimationContainerProvider;

class CallStat extends AbstractStat {

	private final double threshold;
	private final EstimationContainerProvider estContainerProv;
	private final DirMultParameter dirMultPrm;

	private final EstimateDirMult dirMult;
	
	CallStat(
			final double threshold,
			final EstimationContainerProvider estContainerProv,
			final DirMultParameter dirMultPrm) {

		this.threshold 			= threshold;
		this.estContainerProv 	= estContainerProv;
		this.dirMultPrm 		= dirMultPrm;

		dirMult	= new EstimateDirMult(dirMultPrm.getMinkaEstimateParameter()); 
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
		final EstimationContainer[] estContainers = estContainerProv.convert(parallelData);
		double stat;
		if (dirMultPrm.isCalcPValue()) {
			stat = dirMult.getLRT(estContainers);
			// TODO degrees of freedom
			final ChiSquaredDistribution dist = new ChiSquaredDistribution(3); 
			stat = 1 - dist.cumulativeProbability(stat);
		} else {
			stat = dirMult.getScore(estContainers);
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