package lib.stat.dirmult;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;

import lib.stat.dirmult.initalpha.AbstractAlphaInit;
import lib.data.ParallelData;
import lib.data.result.OneStatResult;
import lib.data.result.Result;
import lib.estimate.MinkaEstimateDirMultAlpha;
import lib.stat.AbstractStat;
import lib.stat.AbstractStatFactory;
import lib.stat.DirichletParameter;
import lib.util.Base;
import lib.util.Info;

public class DirMult
extends AbstractStat {

	private final DirMultSampleProvider dirMultSampleProvider;
	private final DirichletParameter dirichletParameter;

	private MinkaEstimateDirMultAlpha minkaEstimateDirMultAlpha;

	private DirMultSample[] dirMultSamples;
	private Info estimateInfo;

	private DecimalFormat decimalFormat;

	private final ChiSquaredDistribution dist = new ChiSquaredDistribution(Base.validValues().length - 1d);
	
	public DirMult(final AbstractStatFactory factory,
			final DirMultSampleProvider dirMultDataProvider,
			final DirichletParameter dirichletParameter) {

		this.dirMultSampleProvider 	= dirMultDataProvider;
		this.dirichletParameter 	= dirichletParameter;
	
		minkaEstimateDirMultAlpha	= new MinkaEstimateDirMultAlpha(dirichletParameter.getMinkaEstimateParameter());
		
		final DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
		otherSymbols.setDecimalSeparator('.');
		otherSymbols.setGroupingSeparator(',');
		decimalFormat = new DecimalFormat("#.##", otherSymbols);
	}

	@Override
	public void addStatResultInfo(final Result statResult) {
		// append content to info field
		final Info info = statResult.getResultInfo();
		if (! isNumericallyStable(dirMultSamples)) {
			info.add("NumericallyInstable");
		}

		if (! estimateInfo.isEmpty()) {
			info.addAll(estimateInfo);
		}
	}
	
	public boolean isNumericallyStable(final DirMultSample[] dirMultSamples) {
		for (final DirMultSample dirMultSample : dirMultSamples) {
			if (! dirMultSample.isNumericallyStable()) {
				return false;
			}
		}

		return true;
	}
	
	public boolean estimate(final DirMultSample dirMultSample, final AbstractAlphaInit alphaInit, final boolean backtrack) {
		// perform an initial guess of alpha
		final double[] initAlpha 	= alphaInit.init(dirMultSample.getDirMultData());
		final double logLikelihood 	= minkaEstimateDirMultAlpha.getLogLikelihood(initAlpha, dirMultSample.getDirMultData());
		dirMultSample.add(initAlpha, logLikelihood);

		// estimate alpha(s), capture and info(s), and store log-likelihood
		return minkaEstimateDirMultAlpha.maximizeLogLikelihood(dirMultSample, estimateInfo, backtrack);
	}
	
	private boolean estimate(final ParallelData parallelData, final AbstractAlphaInit alphaInit, final boolean backtrack) {
		boolean flag = true;
		// estimate alpha(s), capture info(s), and store log-likelihood
		for (final DirMultSample dirMultsample : dirMultSamples) {
			try {
				flag &= estimate(dirMultsample, alphaInit, backtrack);
			} catch (StackOverflowError e) {
				// catch numerical instabilities and report
				dirMultsample.setNumericallyUnstable();
			}
		}
		return flag;
	}
	
	protected double getStatistic(final ParallelData parallelData) {
		// flag to indicated numerical stability of parameter estimation
		estimateInfo = new Info();

		final AbstractAlphaInit defaultAlphaInit 	= dirichletParameter.getMinkaEstimateParameter().getAlphaInit();
		final AbstractAlphaInit fallbackAlphaInit 	= dirichletParameter.getMinkaEstimateParameter().getFallbackAlphaInit();

		dirMultSamples = dirMultSampleProvider.convert(parallelData);
		
		if (! estimate(parallelData, defaultAlphaInit, false)) {
			for (final DirMultSample dirMultsample : dirMultSamples) {
				dirMultsample.clear();
			}
			estimate(parallelData, fallbackAlphaInit, true);
		}
		
		// container for test-statistic
		double stat =  Double.NaN;
		

		// append alpha/iterations/log-likelihood to info info field
		if (dirichletParameter.isShowAlpha()) {
			addShowAlpha();
		}
		
		double tmpLogLikelihood = 0.0;
		for (int conditionIndex = 0; conditionIndex < parallelData.getConditions(); conditionIndex++) {
			tmpLogLikelihood += getDirMultSampleCondition(conditionIndex).getLogLikelihood();
		}
		double NULLLogLikelihood = getDirMultSamplePooled().getLogLikelihood();

		// we want a p-value?
		if (dirichletParameter.isCalcPValue()) {
			// TODO test
			stat = -2 * (NULLLogLikelihood - tmpLogLikelihood);
			stat = 1 - dist.cumulativeProbability(stat);
		} else { // just the log-likelihood ratio
			stat = tmpLogLikelihood - NULLLogLikelihood;
		}

		return stat;
	}

	private  DirMultSample getDirMultSampleCondition(final int conditionIndex) {
		return dirMultSamples[conditionIndex];
	}
	
	private  DirMultSample getDirMultSamplePooled() {
		return dirMultSamples[dirMultSamples.length - 1];
	}
	
	@Override
	public Result calculate(ParallelData parallelData) {
		final double stat = getStatistic(parallelData);
		return new OneStatResult(stat, parallelData);
	}
	
	@Override
	public boolean filter(final Result statResult) {
		final double statValue = statResult.getStat();

		if (Double.isNaN(dirichletParameter.getThreshold())) {
			return false;
		}

		// if p-value interpret threshold as upper bound
		if (dirichletParameter.isCalcPValue()) {
			return dirichletParameter.getThreshold() < statValue;
		}

		// if log-likelihood ratio interpret threshold as lower bound
		return statValue < dirichletParameter.getThreshold();
	}

	protected void addShowAlpha() {
		for (final DirMultSample dirMultSample : dirMultSamples) {
			final String id 			= dirMultSample.getId();
			final int iteration			= dirMultSample.getIteration();
			final double[] initAlpha 	= dirMultSample.getAlpha(0);
			final double[] alpha 		= dirMultSample.getAlpha(iteration);
			final double logLikelihood	= dirMultSample.getLogLikelihood(iteration);
			
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