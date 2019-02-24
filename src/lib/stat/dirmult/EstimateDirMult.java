package lib.stat.dirmult;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import lib.estimate.MinkaEstimateDirMultAlpha;
import lib.estimate.MinkaParameter;
import lib.stat.initalpha.AbstractAlphaInit;
import lib.stat.sample.EstimationSample;
import lib.util.Info;

public class EstimateDirMult {

	private final MinkaParameter minkaParameter;
	private final MinkaEstimateDirMultAlpha minkaEstimateAlpha;

	private DecimalFormat decimalFormat;
	
	private EstimationSample[] estimationSamples;	
	private Info estimateInfo;

	public EstimateDirMult(final MinkaParameter minkaParameter) {
		minkaEstimateAlpha		= new MinkaEstimateDirMultAlpha(minkaParameter);
		this.minkaParameter 	= minkaParameter;
		
		final DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
		otherSymbols.setDecimalSeparator('.');
		otherSymbols.setGroupingSeparator(',');
		decimalFormat = new DecimalFormat("#.##", otherSymbols);
	}

	public void addStatResultInfo(final Info info) {
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
	
	private boolean estimate(final AbstractAlphaInit alphaInit, final boolean backtrack) {
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
	
	public double getScore(final EstimationSample[] estimationSamples) {
		estimate(estimationSamples);
		return getObservedlogLikeliood() - getEstimationSamplePooled().getLogLikelihood();
	}

	private void estimate(final EstimationSample[] estimationSamples) { 
		this.estimationSamples 	= estimationSamples;
		estimateInfo 			= new Info();
	
		final AbstractAlphaInit defaultAlphaInit 	= minkaParameter.getAlphaInit();
		final AbstractAlphaInit fallbackAlphaInit 	= minkaParameter.getFallbackAlphaInit();
	
		if (! estimate(defaultAlphaInit, false)) {
			for (final EstimationSample estimationSample : estimationSamples) {
				estimationSample.clear();
			}
			estimate(fallbackAlphaInit, true);
		}
	}
	
	private double getObservedlogLikeliood() {
		double tmpLogLikelihood = 0.0;
		final int conditions = estimationSamples.length - 1;
		for (int conditionIndex = 0; conditionIndex < conditions; conditionIndex++) {
			tmpLogLikelihood += getEstimationSampleCondition(conditionIndex).getLogLikelihood();
		}
		return tmpLogLikelihood;
	}
	
	public double getLRT(final EstimationSample[] estimationSamples) {
		estimate(estimationSamples);
		return - 2 * (getEstimationSamplePooled().getLogLikelihood() - 
				getObservedlogLikeliood());
	}
	
	private  EstimationSample getEstimationSampleCondition(final int conditionIndex) {
		return estimationSamples[conditionIndex];
	}
	
	private  EstimationSample getEstimationSamplePooled() {
		return estimationSamples[estimationSamples.length - 1];
	}

	public void addShowAlpha() {
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