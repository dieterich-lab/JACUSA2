package lib.stat.dirmult;

import lib.estimate.MinkaParameter;

public interface DirMultParameter {

	public final static double ESTIMATED_ERROR = 0.01;
	
	boolean isShowAlpha();
	void setShowAlpha(boolean showAlpha);

	boolean isCalcPValue();
	void setCalcPValue(boolean calcPValue);

	MinkaParameter getMinkaEstimateParameter();
	void setMinkaParameter(MinkaParameter estimateAlpha);
	
}
