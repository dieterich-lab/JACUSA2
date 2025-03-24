package lib.estimate;

import lib.stat.initalpha.AbstractAlphaInit;
import lib.stat.initalpha.MeanAlphaInit;
import lib.stat.initalpha.MinAlphaInit;

public class MinkaParameter {

	private AbstractAlphaInit alphaInit;
	private AbstractAlphaInit fallbackAlphaInit;
	private int maxIterations;
	private double epsilon;
	
	public MinkaParameter() {
		alphaInit 			= new MeanAlphaInit();
		fallbackAlphaInit	= new MinAlphaInit();
		maxIterations 		= 100;
		epsilon 			= 0.001;
	}

	public AbstractAlphaInit getAlphaInit() {
		return alphaInit;
	}

	public void setAlphaInit(final AbstractAlphaInit alphaInit) {
		this.alphaInit = alphaInit;
	}

	public AbstractAlphaInit getFallbackAlphaInit() {
		return fallbackAlphaInit;
	}

	public void setFallbackAlphaInit(final AbstractAlphaInit fallbackAlphaInit) {
		this.fallbackAlphaInit = fallbackAlphaInit;
	}
	
	public int getMaxIterations() {
		return maxIterations;
	}

	public void setMaxIterations(final int maxIterations) {
		this.maxIterations = maxIterations;
	}

	public double getEpsilon() {
		return epsilon;
	}

	public void setEpsilon(final double epsilon) {
		this.epsilon = epsilon;
	}
	
}
