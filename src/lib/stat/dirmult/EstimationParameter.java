package lib.stat.dirmult;

import lib.estimate.MinkaParameter;

/**
 * DOCUMENT
 */
public interface EstimationParameter {

	public static final double ESTIMATED_ERROR = 0.01;
	
	boolean showAlpha();
	void setShowAlpha(boolean showAlpha);

	boolean calcPValue();
	void setCalcPValue(boolean calcPValue);

	MinkaParameter getMinkaParameter();
	void setMinkaParameter(MinkaParameter estimateAlpha);
	
	int getSubsampleRuns();
	void setSubampleRuns(int subsampleRuns);

}
