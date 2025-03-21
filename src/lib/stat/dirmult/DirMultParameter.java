package lib.stat.dirmult;

import lib.estimate.MinkaParameter;

/**
 * TODO add documentation
 */
public interface DirMultParameter {

	public static final double ESTIMATED_ERROR = 0.01;
	
	boolean showAlpha();
	void setShowAlpha(boolean showAlpha);

	boolean calcPValue();
	void setCalcPValue(boolean calcPValue);

	MinkaParameter getMinkaEstimateParameter();
	void setMinkaParameter(MinkaParameter estimateAlpha);
	
	int getSubsampleRuns();
	void setSubampleRuns(int subsampleRuns);

	/* TODO where to put this
	int getDownsampleRuns();
	void setDownsampleRuns(int downsampleRuns);
	
	double getDownsampleFraction();
	void setDownsampleFraction(double downsampleFraction);
	
	int getRandomSampleRuns();
	void setRandomSampleRuns(int downsampleRuns);
	
	int getLimit();
	void setLimit(int limit);
	*/
}
