package lib.stat.dirmult;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;

import lib.data.ParallelData;
import lib.data.result.OneStatResult;
import lib.data.result.Result;
import lib.estimate.MinkaEstimateDirMultAlpha;
import lib.stat.AbstractStat;
import lib.stat.initalpha.AbstractAlphaInit;
import lib.stat.sample.EstimationSample;
import lib.stat.sample.provider.EstimationSampleProvider;
import lib.util.Base;
import lib.util.Info;

public class DirMult
extends AbstractStat {

	private final EstimationSampleProvider estimationSampleProvider;
	private final DirMultParameter dirMultParameter;

	private MinkaEstimateDirMultAlpha minkaEstimateAlpha;

	private EstimationSample[] estimationSamples;
	private Info estimateInfo;

	private DecimalFormat decimalFormat;

	private final ChiSquaredDistribution dist = new ChiSquaredDistribution(Base.validValues().length - 1d);
	
	public DirMult(
			final EstimationSampleProvider estimationSampleProvider,
			final DirMultParameter dirMultParameter) {

		this.estimationSampleProvider 	= estimationSampleProvider;
		this.dirMultParameter 		= dirMultParameter;
	
		minkaEstimateAlpha				= new MinkaEstimateDirMultAlpha(dirMultParameter.getMinkaEstimateParameter());
		
		final DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
		otherSymbols.setDecimalSeparator('.');
		otherSymbols.setGroupingSeparator(',');
		decimalFormat = new DecimalFormat("#.##", otherSymbols);
	}

	@Override
	public void addStatResultInfo(final Result statResult) {
		// append content to info field
		final Info info = statResult.getResultInfo();
		if (! isNumericallyStable(estimationSamples)) {
			info.add("NumericallyInstable");
		}

		if (! estimateInfo.isEmpty()) {
			info.addAll(estimateInfo);
		}
	}
	
	public boolean isNumericallyStable(final EstimationSample[] estimationSamples) {
		for (final EstimationSample estimationSample : estimationSamples) {
			if (! estimationSample.isNumericallyStable()) {
				return false;
			}
		}

		return true;
	}
	
	public boolean estimate(final EstimationSample estimationSample, final AbstractAlphaInit alphaInit, final boolean backtrack) {
		// perform an initial guess of alpha
		final double[] initAlpha 	= alphaInit.init(estimationSample.getNominalData());
		final double logLikelihood 	= minkaEstimateAlpha.getLogLikelihood(initAlpha, estimationSample.getNominalData());
		estimationSample.add(initAlpha, logLikelihood);

		// estimate alpha(s), capture and info(s), and store log-likelihood
		return minkaEstimateAlpha.maximizeLogLikelihood(estimationSample, estimateInfo, backtrack);
	}
	
	private boolean estimate(final ParallelData parallelData, final AbstractAlphaInit alphaInit, final boolean backtrack) {
		boolean flag = true;
		// estimate alpha(s), capture info(s), and store log-likelihood
		for (final EstimationSample estimationSample : estimationSamples) {
			try {
				flag &= estimate(estimationSample, alphaInit, backtrack);
			} catch (StackOverflowError e) {
				// catch numerical instabilities and report
				estimationSample.setNumericallyUnstable();
			}
		}
		return flag;
	}
	
	protected double getStatistic(final ParallelData parallelData) {
		// flag to indicated numerical stability of parameter estimation
		estimateInfo = new Info();

		final AbstractAlphaInit defaultAlphaInit 	= dirMultParameter.getMinkaEstimateParameter().getAlphaInit();
		final AbstractAlphaInit fallbackAlphaInit 	= dirMultParameter.getMinkaEstimateParameter().getFallbackAlphaInit();

		estimationSamples = estimationSampleProvider.convert(parallelData);
		
		if (! estimate(parallelData, defaultAlphaInit, false)) {
			for (final EstimationSample estimationSample : estimationSamples) {
				estimationSample.clear();
			}
			estimate(parallelData, fallbackAlphaInit, true);
		}
		
		// container for test-statistic
		double stat =  Double.NaN;

		// append alpha/iterations/log-likelihood to info info field
		if (dirMultParameter.isShowAlpha()) {
			addShowAlpha();
		}
		
		double tmpLogLikelihood = 0.0;
		for (int conditionIndex = 0; conditionIndex < parallelData.getConditions(); conditionIndex++) {
			tmpLogLikelihood += getEstimationSampleCondition(conditionIndex).getLogLikelihood();
		}
		double NULLLogLikelihood = getEstimationSamplePooled().getLogLikelihood();

		// we want a p-value?
		if (dirMultParameter.isCalcPValue()) {
			// TODO test
			stat = -2 * (NULLLogLikelihood - tmpLogLikelihood);
			stat = 1 - dist.cumulativeProbability(stat);
		} else { // just the log-likelihood ratio
			stat = tmpLogLikelihood - NULLLogLikelihood;
		}

		return stat;
	}

	private  EstimationSample getEstimationSampleCondition(final int conditionIndex) {
		return estimationSamples[conditionIndex];
	}
	
	private  EstimationSample getEstimationSamplePooled() {
		return estimationSamples[estimationSamples.length - 1];
	}
	
	@Override
	public Result calculate(ParallelData parallelData) {
		final double stat = getStatistic(parallelData);
		return new OneStatResult(stat, parallelData);
	}
	
	@Override
	public boolean filter(final Result statResult) {
		final double statValue = statResult.getStat();

		if (Double.isNaN(dirMultParameter.getThreshold())) {
			return false;
		}

		// if p-value interpret threshold as upper bound
		if (dirMultParameter.isCalcPValue()) {
			return dirMultParameter.getThreshold() < statValue;
		}

		// if log-likelihood ratio interpret threshold as lower bound
		return statValue < dirMultParameter.getThreshold();
	}

	protected void addShowAlpha() {
		for (final EstimationSample estimationSample : estimationSamples) {
			final String id 			= estimationSample.getId();
			final int iteration			= estimationSample.getIteration();
			final double[] initAlpha 	= estimationSample.getAlpha(0);
			final double[] alpha 		= estimationSample.getAlpha(iteration);
			final double logLikelihood	= estimationSample.getLogLikelihood(iteration);
			
			estimateInfo.add("initAlpha" + id, decimalFormat.format(initAlpha[0]));			
			for (int i = 1; i < initAlpha.length; ++i) {
				estimateInfo.add("initAlpha" + id, ":");
				estimateInfo.add("initAlpha" + id, decimalFormat.format(initAlpha[i]));
			}
			
			estimateInfo.add("alpha" + id, decimalFormat.format(alpha[0]));			
			for (int i = 1; i < alpha.length; ++i) {
				estimateInfo.add("alpha" + id, ":");
				estimateInfo.add("alpha" + id, decimalFormat.format(alpha[i]));
			}
		
			estimateInfo.add("iteration" + id, Integer.toString(iteration));
			estimateInfo.add("logLikelihood" + id, Double.toString(logLikelihood));
		}
	}
	
}