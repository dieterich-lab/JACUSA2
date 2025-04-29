package lib.cli.parameter;

import lib.stat.dirmult.EstimationParameter;

public interface HasInsertionParameter {

	EstimationParameter getInsertionEstimationParameter();
	void setInsertionParameter(EstimationParameter dirMultParameter);
	
}
