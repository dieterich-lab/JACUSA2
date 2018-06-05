package jacusa.method.call.statistic;

import jacusa.cli.parameters.CallParameter;
import jacusa.estimate.MinkaEstimateParameters;
import jacusa.method.call.statistic.dirmult.initalpha.AbstractAlphaInit;
import jacusa.method.call.statistic.dirmult.initalpha.MinAlphaInit;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import htsjdk.samtools.util.SequenceUtil;
import lib.cli.options.Base;
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

	public AbstractDirichletStatistic(final Option option, final CallParameter parameter) {
		super(option);
		this.parameter = parameter;
	}

	protected AbstractDirichletStatistic(final Option option,
			final MinkaEstimateParameters estimateAlpha, final CallParameter parameter) {

		super(option);
		this.parameter 		= parameter;
		final int n 		= SequenceUtil.VALID_BASES_UPPER.length;

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
	 * @param replicateData
	 * @param bases
	 * @param matrix
	 */
	protected void populate(
			final Base[] bases,
			final T[] replicateData, 
			double[][] matrix) {
		for (int i = 0; i < replicateData.length; ++i) {
			T data = replicateData[i];
	
			populate(data, bases, matrix[i]);
		}
	}

	/**
	 * 
	 * @param data
	 * @param bases
	 * @param pileupErrorVector
	 * @param dataVector
	 */
	protected abstract void populate(
			final T data, 
			final Base[] bases,
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
			final Base[] bases,
			final T[] replicateData,
			final boolean backtrack ) {
		// populate pileupMatrix with values to be modeled
		final double[][] dataMatrix  = new double[replicateData.length][alpha.length];

		// populate dataMatrix with values to be modeled
		populate(bases, replicateData, dataMatrix);
		// perform an initial guess of alpha
		System.arraycopy(alphaInit.init(bases, dataMatrix), 0, initAlphaValues, 0, alpha.length);

		// store initial alpha guess
		System.arraycopy(initAlphaValues, 0, alpha, 0, alpha.length);

		// estimate alpha(s), capture and info(s), and store log-likelihood
		// return estimateAlpha.maximizeLogLikelihood(alpha, baseIndexs, dataMatrix, condition, estimateInfo, backtrack);
		return estimateAlpha.maximizeLogLikelihood(condition, alpha, bases, dataMatrix, estimateInfo, backtrack);
	}
	
	@Override
	public double getStatistic(final ParallelData<T> parallelData) {
		// base index mask; can be ACGT or only observed bases in parallelPileup
		final Base bases[] = getBase(parallelData);
		// number of globally considered bases, normally 4 : ACGT
		int baseN = SequenceUtil.VALID_BASES_UPPER.length;

		// flag to indicated numerical stability of parameter estimation
		numericallyStable = true;
		estimateInfo = new Info();

		int conditions = parallelData.getConditions();
		
		// parameters for distribution
		alpha = new double[conditions + 1][baseN];
		logLikelihood = new double[conditions + 1];
		initAlpha = new double[conditions + 1][baseN];
		iterations = new int[conditions + 1];
		
		// estimate alpha(s), capture and info(s), and store log-likelihood
		boolean isReset = false;
		for (int conditionIndex = 0; conditionIndex < conditions; conditionIndex++) {
			logLikelihood[conditionIndex] = estimate(Integer.toString(conditionIndex + 1),  
					alpha[conditionIndex], initAlpha[conditionIndex], estimateAlpha.getAlphaInit(), 
					bases, parallelData.getData(conditionIndex), false);
			iterations[conditionIndex] = estimateAlpha.getIterations();
			isReset |= estimateAlpha.isReset();
		}
		// pooled data
		int pooledIndex = conditions;
		logLikelihood[pooledIndex] = estimate("P", 
				alpha[pooledIndex], initAlpha[pooledIndex], estimateAlpha.getAlphaInit(), 
				bases, parallelData.getCombinedData(), false);
		iterations[pooledIndex] = estimateAlpha.getIterations();
		isReset |= estimateAlpha.isReset();
 	
		if (isReset) {
			for (int conditionIndex = 0; conditionIndex < conditions; conditionIndex++) {
				logLikelihood[conditionIndex] = estimate(Integer.toString(conditionIndex + 1), 
						alpha[conditionIndex], initAlpha[conditionIndex], fallbackAlphaInit, bases, parallelData.getData(conditionIndex), true);
				iterations[conditionIndex] = estimateAlpha.getIterations();
			}
			
			logLikelihood[pooledIndex] = estimate("P", 
					alpha[pooledIndex], initAlpha[pooledIndex], fallbackAlphaInit, bases, parallelData.getCombinedData(), true);
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
		// if log-likelihood ratio and value not set give all results
		if (parameter.getStatisticParameters().getThreshold() == Double.NaN) {
			return false;
		}
		
		// if p-value interpret threshold as upper bound
		if (calcPValue) {
			return threshold < value;
		}
		
		// if log-likelihood ratio interpret threshold as lower bound 
		return value < threshold;
	}

	@Override
	protected Options getOptions() {
		final Options options = new Options();

		options.addOption(Option.builder("epsilon")
				.hasArg(true)
				.desc("Default: " + estimateAlpha.getEpsilon())
				.build());

		options.addOption(Option.builder("maxIterations")
				.hasArg(true)
				.desc("Default: " + estimateAlpha.getMaxIterations())
				.build());

		options.addOption(Option.builder("onlyObserved")
				.hasArg(true)
				.desc("")
				.build());
		
		options.addOption(Option.builder("calculateP-value")
				.hasArg(true)
				.desc("")
				.build());
		
		options.addOption(Option.builder("showAlpha")
				.hasArg(true)
				.desc("")
				.build());
		
		return options;
	}
	
	@Override
	public void processCLI(final CommandLine cmd) {
		// format: -u DirMult:epsilon=<epsilon>:maxIterations=<maxIterions>:onlyObserved

		// ignore any first array element of s (e.g.: s[0] = "-u DirMult") 
		for (final Option option : cmd.getOptions()) {
			final String opt = option.getOpt();
			switch (opt) {
			case "epsilon":
				estimateAlpha.setEpsilon(Double.parseDouble(cmd.getOptionValue(opt)));
				break;
				
			case "maxIterations":
				estimateAlpha.setMaxIterations(Integer.parseInt(cmd.getOptionValue(opt)));
				break;
				
			case "onlyObserved":
				setOnlyObservedBases(true);
				break;

			case "calculateP-value":
				setCalcPValue(true);
				break;
				
			case "showAlpha":
				setShowAlpha(true);
				break;

			 /*else if(key.equals("initAlpha")) {
				// parse arguments by factory
				AbstractAlphaInit alphaInit = alphaInitFactory.processCLI(value);
				estimateAlpha.setAlphaInit(alphaInit);
				r = true;
				
			}*/

			default:
				break;
			}
		}
	}

	/**
	 * 
	 * @param parallelData
	 * @return
	 */
	protected Base[] getBase(final ParallelData<T> parallelData) {
		if (onlyObservedBases) {
			final Set<Base> tmp = parallelData.getCombinedPooledData().getPileupCount().getBaseCallCount().getAlleles();
			final Base[] alleles = new Base[tmp.size()];
			int i = 0;
			for (final Base base : tmp) {
				alleles[i] = base;
				++i;
			}
			return alleles;
		}

		return Base.validValues();
	}
	
	public MinkaEstimateParameters getEstimateAlpha() {
		return  estimateAlpha;
	}

	public void setShowAlpha(boolean showAlpha) {
		this.showAlpha = showAlpha;
	}
	
	public void setCalcPValue(boolean calcPValue) {
		this.calcPValue = calcPValue;
	}

	public void setOnlyObservedBases(boolean onlyObservedBases) {
		this.onlyObservedBases = onlyObservedBases;
	}

}
