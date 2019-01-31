package lib.stat;

import lib.estimate.MinkaEstimateParameter;

public class DirichletParameter {

	public final static double ESTIMATED_ERROR = 0.01;
	
	private boolean showAlpha;
	private boolean calcPValue;
	
	private double estimatedError; 
	
	private MinkaEstimateParameter minkaEstimateParameter;

	private double threshold;
	
	public DirichletParameter() {
		showAlpha 			= false;
		calcPValue			= false;

		estimatedError 		= ESTIMATED_ERROR;

		minkaEstimateParameter	= new MinkaEstimateParameter();

		threshold			= Double.NaN;
	}

	public boolean isShowAlpha() {
		return showAlpha;
	}

	public void setShowAlpha(final boolean showAlpha) {
		this.showAlpha = showAlpha;
	}

	public boolean isCalcPValue() {
		return calcPValue;
	}

	public void setCalcPValue(final boolean calcPValue) {
		this.calcPValue = calcPValue;
	}

	public double getEstimatedError() {
		return estimatedError;
	}

	public void setEstimatedError(final double estimatedError) {
		this.estimatedError = estimatedError;
	}
	
	public MinkaEstimateParameter getMinkaEstimateParameter() {
		return minkaEstimateParameter;
	}

	public void setMinkaEstimateParameter(final MinkaEstimateParameter estimateAlpha) {
		this.minkaEstimateParameter = estimateAlpha;
	}

	public double getThreshold() {
		return threshold;
	}

	public void setThreshold(final double threshold) {
		this.threshold = threshold;
	}
	
}
