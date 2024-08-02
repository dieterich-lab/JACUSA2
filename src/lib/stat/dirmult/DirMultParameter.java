package lib.stat.dirmult;

import lib.estimate.MinkaParameter;

/**
 * TODO
 */
public interface DirMultParameter {

	public static final double ESTIMATED_ERROR = 0.01;
	
	boolean isShowAlpha();
	void setShowAlpha(boolean showAlpha);

	boolean isCalcPValue();
	void setCalcPValue(boolean calcPValue);

	MinkaParameter getMinkaEstimateParameter();
	void setMinkaParameter(MinkaParameter estimateAlpha);
	
	int getRuns();
	void setRuns(int runs);

	int getLimit();
	void setLimit(int limit);
}
