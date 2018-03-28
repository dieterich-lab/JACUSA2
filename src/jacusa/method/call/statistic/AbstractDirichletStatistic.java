package jacusa.method.call.statistic;

import jacusa.cli.parameters.CallParameter;
import jacusa.estimate.MinkaEstimateParameters;
import jacusa.filter.factory.AbstractFilterFactory;
import jacusa.method.call.statistic.dirmult.initalpha.AbstractAlphaInit;
import jacusa.method.call.statistic.dirmult.initalpha.MinAlphaInit;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import lib.data.AbstractData;
import lib.data.ParallelData;
import lib.data.has.HasPileupCount;
import lib.phred2prob.Phred2Prob;
import lib.util.Info;

public abstract class AbstractDirichletStatistic<T extends AbstractData & HasPileupCount>
extends AbstractStatisticCalculator<T> {

	protected final CallParameter parameter;
	
	protected Phred2Prob phred2Prob;

	protected boolean onlyObservedBases;
	protected boolean showAlpha;
	protected boolean calcPValue;
	
	protected double[][] alpha;
	protected double[][] initAlpha;
	
	protected int[] iterations;

	protected double[] logLikelihood;
	
	protected boolean numericallyStable;
	protected Info estimateInfo;
	
	protected MinkaEstimateParameters estimateAlpha;
	
	protected AbstractAlphaInit fallbackAlphaInit;
	
	protected DecimalFormat decimalFormat;

	public AbstractDirichletStatistic(final String name, final String desc, final CallParameter parameter) {
		super(name, desc);
		this.parameter = parameter;
	}

	protected AbstractDirichletStatistic(final String name, final String desc, final double threshold, 
			final MinkaEstimateParameters estimateAlpha, final CallParameter parameter) {

		super(name, desc, threshold);
		this.parameter 		= parameter;
		final int n 		= parameter.getBaseConfig().getBases().length;

		phred2Prob 			= Phred2Prob.getInstance(n);
		onlyObservedBases 	= false;
		showAlpha			= false;

		this.estimateAlpha	= estimateAlpha;
		fallbackAlphaInit	= new MinAlphaInit();
		
		// alphaInitFactory	= new AlphaInitFactory(getAlphaInits());
		
		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
		otherSymbols.setDecimalSeparator('.');
		otherSymbols.setGroupingSeparator(',');
		decimalFormat = new DecimalFormat("#.##", otherSymbols);
	}
	
	
	/**
	 * 
	 * @param data
	 * @param baseIndexs
	 * @param pileupMatrix
	 */
	protected void populate(
			final int[] baseIndexs,
			final T[] data, 
			double[][] pileupMatrix) {
		for (int i = 0; i < data.length; ++i) {
			T d = data[i];
	
			populate(d, baseIndexs, pileupMatrix[i]);
		}
	}

	/**
	 * 
	 * @param data
	 * @param baseIndexs
	 * @param pileupErrorVector
	 * @param dataVector
	 */
	protected abstract void populate(
			final T data, 
			final int[] baseIndexs,
			double[] dataVector);

	
	
	@Override
	public void addInfo(final Info info) {
		// append content to info field
		if (! isNumericallyStable()) {
			info.add("NumericallyInstable");
		}

		if (! estimateInfo.isEmpty()) {
			info.addAll(estimateInfo);
		}
	}

	public double[] getAlpha(final int condition) {
		return alpha[condition];
	}
	public double[] getAlphaPooled() {
		return alpha[alpha.length - 1];
	}
	public double[] getInitAlpha(final int condition) {
		return initAlpha[condition];
	}
	public double[] getInitAlphaPooled() {
		return initAlpha[initAlpha.length - 1];
	}
	public double getLogLikelihood(final int condition) {
		return logLikelihood[condition];
	}
	public double getLogLikelihoodPooled() {
		return logLikelihood[logLikelihood.length - 1];
	}
	public boolean isNumericallyStable() {
		return numericallyStable;
	}
	
	public double estimate(
			final String condition, 
			double[] alpha, 
			double[] initAlphaValues, 
			final AbstractAlphaInit alphaInit,
			final int[] baseIndexs,
			final T[] dataMatrix,
			final boolean backtrack ) {
		// populate pileupMatrix with values to be modeled
		final double[][] datamatrix  = new double[dataMatrix.length][alpha.length];

		// populate dataMatrix with values to be modeled
		populate(baseIndexs, dataMatrix, datamatrix);
		// perform an initial guess of alpha
		System.arraycopy(alphaInit.init(baseIndexs, datamatrix), 0, initAlphaValues, 0, alpha.length);

		// store initial alpha guess
		System.arraycopy(initAlphaValues, 0, alpha, 0, alpha.length);

		// estimate alpha(s), capture and info(s), and store log-likelihood
		// return estimateAlpha.maximizeLogLikelihood(alpha, baseIndexs, datamatrix, condition, estimateInfo, backtrack);
		return estimateAlpha.maximizeLogLikelihood(condition, alpha, baseIndexs, datamatrix, estimateInfo, backtrack);
	}
	
	@Override
	public double getStatistic(final ParallelData<T> parallelData) {
		// base index mask; can be ACGT or only observed bases in parallelPileup
		final int baseIndexs[] = getBaseIndex(parallelData);
		// number of globally considered bases, normally 4 : ACGT
		int baseN = parameter.getBaseConfig().getBases().length;

		// flag to indicated numerical stability of parameter estimation
		numericallyStable = true;
		estimateInfo = new Info();

		int conditions = parallelData.getConditions();
		
		// parameters for distribution
		alpha = new double[conditions + 1][baseN];
		initAlpha = new double[conditions + 1][baseN];

		// estimate alpha(s), capture and info(s), and store log-likelihood
		boolean isReset = false;
		for (int conditionIndex = 0; conditionIndex < conditions; conditionIndex++) {
			logLikelihood[conditionIndex] = estimate(Integer.toString(conditionIndex + 1),  
					alpha[conditionIndex], initAlpha[conditionIndex], estimateAlpha.getAlphaInit(), 
					baseIndexs, parallelData.getData(conditionIndex), false);
			iterations[conditionIndex] = estimateAlpha.getIterations();
			isReset |= estimateAlpha.isReset();
		}
		// pooled data
		int pooledIndex = conditions;
		logLikelihood[pooledIndex] = estimate("P", alpha[pooledIndex], 
				initAlpha[pooledIndex], estimateAlpha.getAlphaInit(), 
				baseIndexs, parallelData.getCombinedData(), false);
		iterations[pooledIndex] = estimateAlpha.getIterations();
		isReset |= estimateAlpha.isReset();

		if (isReset) {
			for (int conditionIndex = 0; conditionIndex < conditions; conditionIndex++) {
				logLikelihood[conditionIndex] = estimate(Integer.toString(conditionIndex + 1), 
						alpha[conditionIndex], initAlpha[conditionIndex], fallbackAlphaInit, baseIndexs, parallelData.getData(conditionIndex), true);
				iterations[conditionIndex] = estimateAlpha.getIterations();
			}
			
			logLikelihood[pooledIndex] = estimate("P", 
					alpha[pooledIndex], initAlpha[pooledIndex], fallbackAlphaInit, baseIndexs, parallelData.getCombinedData(), true);
			iterations[pooledIndex] = estimateAlpha.getIterations();

		}

		// container for test-statistic
		double stat = Double.NaN;
		try {

			// append alpha/iterations/log-likelihood to info info field
			if (showAlpha) {
				for (int conditionIndex = 0; conditionIndex < conditions; conditionIndex++) {
				
					estimateInfo.add("alpha" + (conditionIndex + 1), decimalFormat.format(alpha[conditionIndex][0]));			
					for (int i = 1; i < alpha[conditionIndex].length; ++i) {
						estimateInfo.add("alpha" + (conditionIndex + 1), ":");
						estimateInfo.add("alpha" + (conditionIndex + 1), decimalFormat.format(alpha[conditionIndex][i]));
					}
									
					for (int i = 1; i < alpha[pooledIndex].length; ++i) {
						estimateInfo.add("alphaP", ":");
						estimateInfo.add("alphaP", decimalFormat.format(alpha[pooledIndex][i]));
					}
				
					estimateInfo.add("initAlpha" + (conditionIndex + 1), decimalFormat.format(initAlpha[conditionIndex][0]));			
					for (int i = 1; i < initAlpha[conditionIndex].length; ++i) {
						estimateInfo.add("initAlpha" + (conditionIndex + 1), ":");
						estimateInfo.add("initAlpha" + (conditionIndex + 1), decimalFormat.format(initAlpha[conditionIndex][i]));
					}
				
					estimateInfo.add("initAlphaP", decimalFormat.format(initAlpha[pooledIndex][0]));			
					for (int i = 1; i < initAlpha[pooledIndex].length; ++i) {
						estimateInfo.add("initAlphaP", ":");
						estimateInfo.add("initAlphaP", decimalFormat.format(initAlpha[pooledIndex][i]));
					}
				
					estimateInfo.add("iterations" + (conditionIndex + 1), Integer.toString(iterations[conditionIndex]));
					estimateInfo.add("iterationsP", Integer.toString(iterations[pooledIndex]));
				
					estimateInfo.add("logLikelihood" + (conditionIndex + 1), Double.toString(logLikelihood[conditionIndex]));
					estimateInfo.add("logLikelihoodP", Double.toString(logLikelihood[pooledIndex]));
				}
			}
			
			double tmpLogLikelihood = 0.0;
			for (int conditionIndex = 0; conditionIndex < conditions; conditionIndex++) {
				tmpLogLikelihood += logLikelihood[conditionIndex];
			}
			
			// we want a p-value?
			if (calcPValue) {
				// FIXME HOW MANY DEGREES OF FREEDOM
				stat = -2 * (logLikelihood[pooledIndex] - tmpLogLikelihood);
				// FIXME use THIS with other implementation ChiSquareDist dist = new ChiSquareDist(baseIndexs.length - 1);
				// FIXME stat = 1 - dist.cdf(stat);
			} else { // just the log-likelihood ratio
				stat = tmpLogLikelihood - logLikelihood[pooledIndex];
			}
		} catch (StackOverflowError e) {
			// catch numerical instabilities and report
			numericallyStable = false;
			return stat;
		}

		return stat;
	}

	/**
	 * Pretty print alpha.
	 * Debug function.
	 * 
	 * @param alphas
	 */
	/*
	protected void printAlpha(double[] alphas) {
		StringBuilder sb = new StringBuilder();
		for (double alpha : alphas) {
			sb.append(Double.toString(alpha));
			sb.append("\t");
		}
		System.out.println(sb.toString());
	}
	*/

	@Override
	public boolean filter(double value, double threshold) {
		// if p-value interpret threshold as upper bound
		if (calcPValue) {
			return threshold < value;
		}
		
		// if log-likelihood ratio and value not set give all results
		if (parameter.getStatisticParameters().getThreshold() == Double.NaN) {
			return false;
		}
		
		// if log-likelihood ratio interpret threshold as lower bound 
		return value < threshold;
	}

	
	@Override
	public boolean processCLI(String line) {
		// format: -u DirMult:epsilon=<epsilon>:maxIterations=<maxIterions>:onlyObserved
		String[] s = line.split(Character.toString(AbstractFilterFactory.OPTION_SEP));
		// indicates if a CLI has been successfully parsed
		boolean r = false;

		// ignore any first array element of s (e.g.: s[0] = "-u DirMult") 
		for (int i = 1; i < s.length; ++i) {
			// kv := "key[=value]" 
			String[] kv = s[i].split("=");
			String key = kv[0];
			// value may be empty for options without arguments, e.g.: "onlyObserved"
			String value = new String();
			if (kv.length == 2) {
				value = kv[1];
			}

			// parse key and do something
			if (key.equals("epsilon")) { 
				estimateAlpha.setEpsilon(Double.parseDouble(value));
				r = true;
			} else if(key.equals("maxIterations")) {
				estimateAlpha.setMaxIterations(Integer.parseInt(value));
				r = true;
			} else if(key.equals("onlyObserved")) {
				onlyObservedBases = true;
				r = true;
			} else if(key.equals("calculateP-value")) {
				calcPValue = true;
				r = true;
			} else if(key.equals("showAlpha")) {
				showAlpha = true;
				r = true;
			} /*else if(key.equals("initAlpha")) {
				// parse arguments by factory
				AbstractAlphaInit alphaInit = alphaInitFactory.processCLI(value);
				estimateAlpha.setAlphaInit(alphaInit);
				r = true;
				
			}*/
		}

		return r;
	}

	/**
	 * 
	 * @param parallelData
	 * @return
	 */
	protected int[] getBaseIndex(final ParallelData<T> parallelData) {
		if (onlyObservedBases) {
			return parallelData.getCombinedPooledData().getPileupCount().getBaseCallCount().getAlleles();
		}

		return parameter.getBaseConfig().getBaseIndex();
	}
	
	public MinkaEstimateParameters getEstimateAlpha() {
		return  estimateAlpha;
	}

	public void setShowAlpha(boolean showAlpha) {
		this.showAlpha = showAlpha;
	}

	public void setOnlyObservedBases(boolean onlyObservedBases) {
		this.onlyObservedBases = onlyObservedBases;
	}
	
}
