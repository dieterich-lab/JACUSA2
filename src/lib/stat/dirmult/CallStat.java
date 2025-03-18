package lib.stat.dirmult;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;

import lib.data.ParallelData;
import lib.data.result.OneStatResult;
import lib.data.result.Result;
import lib.estimate.MinkaEstimateDirMultAlpha;
import lib.stat.AbstractStat;
import lib.stat.estimation.EstimationContainer;
import lib.stat.estimation.provider.EstimationContainerProvider;
import lib.util.ExtendedInfo;

class CallStat extends AbstractStat {

	private final double threshold;
	private final EstimationContainerProvider estimationContainerProvider;
	private final DirMultParameter dirMultParameter;

	private final MinkaEstimateDirMultAlpha estimateDirMultAlpha;

	CallStat(final double threshold,
			final EstimationContainerProvider estimationContainerProvider,
			final DirMultParameter dirMultParameter) {
		super();
		
		this.threshold 						= threshold;
		this.estimationContainerProvider 	= estimationContainerProvider;
		this.dirMultParameter 				= dirMultParameter;

		estimateDirMultAlpha 				= new MinkaEstimateDirMultAlpha(dirMultParameter.getMinkaEstimateParameter());
	}

	// TODO remove
	@Override
	protected void postProcess(final Result result, final int valueIndex) {
		if (dirMultParameter.showAlpha()) {
			estimateDirMultAlpha.addAlphaValues(result.getResultInfo(valueIndex));
		}
		estimateDirMultAlpha.addStatResultInfo(result.getResultInfo(valueIndex));
	}

	@Override
	public Result calculate(ParallelData parallelData) {
		final EstimationContainer[] estimationContainers = estimationContainerProvider.convert(parallelData);
		double stat;
		final ExtendedInfo resultInfo = new ExtendedInfo(parallelData.getReplicates());
		if (dirMultParameter.calcPValue()) {
			stat = estimateDirMultAlpha.getLRT(estimationContainers, resultInfo);
			// TODO degrees of freedom
			final ChiSquaredDistribution dist = new ChiSquaredDistribution(3);
			stat = 1 - dist.cumulativeProbability(stat);
		} else {
			stat = estimateDirMultAlpha.getScore(estimationContainers, resultInfo);
		}
		return new OneStatResult(stat, parallelData, resultInfo);
	}

	@Override
	public boolean filter(final Result statResult) {
		final double statValue = statResult.getScore();

		if (Double.isNaN(threshold)) {
			return false;
		}

		// if p-value interpret threshold as upper bound
		if (dirMultParameter.calcPValue()) {
			return threshold < statValue;
		}

		// if log-likelihood ratio interpret threshold as lower bound
		return statValue < threshold;
	}
	
}