package lib.stat.dirmult;

import lib.estimate.MinkaParameter;

public abstract class AbstractDirMultParameter implements DirMultParameter {
	
	private boolean showAlpha;
	private boolean calcPValue;
	private MinkaParameter minkaParameter;
	private double threshold; 
	
	public AbstractDirMultParameter(
			final boolean showAlpha,
			final boolean calcPValue,
			final MinkaParameter minkaParameter,
			final double threshold) {
		this.showAlpha 		= showAlpha;
		this.calcPValue		= calcPValue;
		this.minkaParameter	= minkaParameter;
		this.threshold		= threshold;
	}

	@Override
	public boolean isShowAlpha() {
		return showAlpha;
	}

	@Override
	public void setShowAlpha(final boolean showAlpha) {
		this.showAlpha = showAlpha;
	}

	@Override
	public boolean isCalcPValue() {
		return calcPValue;
	}

	@Override
	public void setCalcPValue(final boolean calcPValue) {
		this.calcPValue = calcPValue;
	}
	
	@Override
	public MinkaParameter getMinkaEstimateParameter() {
		return minkaParameter;
	}

	@Override
	public void setMinkaParameter(final MinkaParameter minkaParameter) {
		this.minkaParameter = minkaParameter;
	}

	@Override
	public double getThreshold() {
		return threshold;
	}

	@Override
	public void setThreshold(final double threshold) {
		this.threshold = threshold;
	}
	
}
