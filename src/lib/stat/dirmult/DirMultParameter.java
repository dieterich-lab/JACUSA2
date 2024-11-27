package lib.stat.dirmult;

import lib.estimate.MinkaParameter;

/**
 * TODO add documentation
 */
public interface DirMultParameter {

	public static final double ESTIMATED_ERROR = 0.01;
	
	boolean isShowAlpha();
	void setShowAlpha(boolean showAlpha);

	boolean isCalcPValue();
	void setCalcPValue(boolean calcPValue);

	MinkaParameter getMinkaEstimateParameter();
	void setMinkaParameter(MinkaParameter estimateAlpha);
	
	int getSubsampleRuns();
	void setSubampleRuns(int subsampleRuns);

	int getDownsampleRuns();
	void setDownsampleRuns(int downsampleRuns);
	
	double getDownsampleFraction();
	void setDownsampleFraction(double downsampleFraction);
	
	int getLimit();
	void setLimit(int limit);
}
