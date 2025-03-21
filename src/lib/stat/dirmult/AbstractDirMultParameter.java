package lib.stat.dirmult;

import lib.estimate.MinkaParameter;

/**
 * TODO add documentation
 */
public abstract class AbstractDirMultParameter implements DirMultParameter {
	
	private boolean showAlpha;
	private boolean calcPValue;
	private MinkaParameter minkaParameter;
	
	private int subsampleRuns = 0;
	
	public AbstractDirMultParameter(
			final boolean showAlpha,
			final boolean calcPValue,
			final MinkaParameter minkaParameter) {

		this.showAlpha 		= showAlpha;
		this.calcPValue		= calcPValue;
		this.minkaParameter	= minkaParameter;
	}

	@Override
	public boolean showAlpha() {
		return showAlpha;
	}

	@Override
	public void setShowAlpha(final boolean showAlpha) {
		this.showAlpha = showAlpha;
	}

	@Override
	public boolean calcPValue() {
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

	public int getSubsampleRuns() {
		return subsampleRuns;
	}

	@Override
	public void setSubampleRuns(int runs) {
		this.subsampleRuns = runs;
	}
	
}
