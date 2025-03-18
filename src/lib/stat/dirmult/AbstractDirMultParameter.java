package lib.stat.dirmult;

import lib.estimate.MinkaParameter;

/**
 * TODO add documentation
 */
public abstract class AbstractDirMultParameter implements DirMultParameter {
	
	private boolean showAlpha;
	private boolean calcPValue;
	private MinkaParameter minkaParameter;
	
	/* TODO remove
	private int subsampleRuns = 0;
	private int downsampleRuns = 0;
	private int randomSampleRuns = 0;
	private double downsampleFraction = 0.0;
	*/
	
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
	

	@Override
	public int getLimit() {
		return limit;
	}
	
	/* TODO remove
	@Override
	public int getSubsampleRuns() {
		return subsampleRuns;
	}
	
	@Override
	public int getDownsampleRuns() {
		return downsampleRuns;
	}
	
	@Override
	public double getDownsampleFraction() {
		return downsampleFraction;
	}
	
	@Override
	public int getRandomSampleRuns() {
		return randomSampleRuns;
	}
	*/
	
	@Override
	public void setLimit(int limit) {
		this.limit = limit;
	}
	
	/* TODO remove
	@Override
	public void setSubampleRuns(int runs) {
		this.subsampleRuns = runs;
	}

	@Override
	public void setDownsampleRuns(final int downsampleRuns) {
		this.downsampleRuns = downsampleRuns;
	}
	
	@Override
	public void setDownsampleFraction(final double downsampleFraction) {
		this.downsampleFraction = downsampleFraction;
	}
	
	@Override
	public void setRandomSampleRuns(final int randomSampleRuns) {
		this.randomSampleRuns = randomSampleRuns;
	}
	*/ 
	
}
