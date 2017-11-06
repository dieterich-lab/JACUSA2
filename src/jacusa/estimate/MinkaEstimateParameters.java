package jacusa.estimate;

import jacusa.method.call.statistic.dirmult.initalpha.AbstractAlphaInit;
import jacusa.method.call.statistic.dirmult.initalpha.MeanAlphaInit;
import jacusa.util.Info;

import org.apache.commons.math3.special.Gamma;

public abstract class MinkaEstimateParameters {

	protected AbstractAlphaInit alphaInit;
	
	// options for paremeters estimation
	protected int maxIterations;
	protected double epsilon; 
	protected int iterations;
	protected boolean reset;
	
	public MinkaEstimateParameters() {
		alphaInit = new MeanAlphaInit();
		maxIterations = 100;
		epsilon = 0.001;
		reset = false;
	}

	public boolean isReset() {
		return reset;
	}

	public int getIterations() {
		return iterations;
	}
	
	// estimate alpha and returns loglik
	public abstract double maximizeLogLikelihood(
			final String condition,
			final double[] alphaOld, 
			final int[] baseIndexs,
			final double[][] dataMatrix,
			final Info resultInfo,
			final boolean backtrack);

	protected double[] backtracking(
			final double[] alpha, 
			final int[] baseIndexs, 
			final double[] gradient, 
			final double b, 
			final double[] Q) {
		double[] alphaNew = new double[alpha.length];

		// try smaller newton steps
		double lamba = 1.0;
		// decrease by
		double offset = 0.1;

		while (lamba >= 0.0) {
			lamba = lamba - offset;

			boolean admissible = true;
			// adjust alpha with smaller newton step
			for (int baseI : baseIndexs) {
				alphaNew[baseI] = alpha[baseI] - lamba * (gradient[baseI] - b) / Q[baseI];
				// check if admissible
				if (alphaNew[baseI] < 0.0) {
					admissible = false;
					break;
				}
			}

			if (admissible) {
				return alphaNew;
			}
		}

		// could not find alpha(s)
		return null;
	}
	
	// calculate likelihood
	protected abstract double getLogLikelihood(
			final double[] alpha, 
			final int[] baseIndexs, 
			final double[][] pileupMatrix);

	protected double digamma(double x) {
		return Gamma.digamma(x);
	}

	protected double trigamma(double x) {
		return Gamma.trigamma(x);
	}

	public int getMaxIterations() {
		return maxIterations;
	}

	public void setMaxIterations(int maxIterations) {
		this.maxIterations = maxIterations;
	}

	public double getEpsilon() {
		return epsilon;
	}

	public void setEpsilon(double epsilon) {
		this.epsilon = epsilon;
	}

	public AbstractAlphaInit getAlphaInit() {
		return alphaInit;
	}
	
	public void setAlphaInit(AbstractAlphaInit alphaInit) {
		this.alphaInit = alphaInit;
	}

}
