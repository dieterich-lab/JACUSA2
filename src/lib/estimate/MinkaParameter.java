package lib.estimate;

import lib.stat.initalpha.AbstractAlphaInit;
import lib.stat.initalpha.MeanAlphaInit;
import lib.stat.initalpha.MinAlphaInit;

/**
 * TODO comments
 */
public class MinkaParameter {

	private AbstractAlphaInit alphaInit;
	private AbstractAlphaInit fallbackAlphaInit;
	private int maxIterations;
	private double epsilon;
	private ExtraLetters extraLetters;
	
	
	public MinkaParameter() {
		alphaInit 			= new MeanAlphaInit();
		fallbackAlphaInit	= new MinAlphaInit();
		maxIterations 		= 100;
		epsilon 			= 0.001;
		extraLetters		= null;
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
	

	public ExtraLetters getExtraLetters() {
		return this.extraLetters;
	}

	public void setExtraLetters(final ExtraLetters extraLetters) {
		this.extraLetters = extraLetters;
	}

	public enum ExtraLetters {
		Insertion, Deletion, INDEL
	}

}

