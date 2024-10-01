package lib.stat.dirmult;

import lib.estimate.MinkaParameter;

/**
 * TODO add documentation
 */
public abstract class AbstractDirMultParameter implements DirMultParameter {
	
	private boolean showAlpha;
	private boolean calcPValue;
	private MinkaParameter minkaParameter;
	
	private int runs = 0;
	private int limit = 0;
	
	public AbstractDirMultParameter(
			final boolean showAlpha,
			final boolean calcPValue,
			final MinkaParameter minkaParameter) {

		this.showAlpha 		= showAlpha;
		this.calcPValue		= calcPValue;
		this.minkaParameter	= minkaParameter;
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
	public int getLimit() {
		return limit;
	}
	
	@Override
	public int getSampleRuns() {
		return runs;
	}
	
	@Override
	public void setLimit(int limit) {
		this.limit = limit;
	}
	
	@Override
	public void setRuns(int runs) {
		this.runs = runs;
	}
	
}
