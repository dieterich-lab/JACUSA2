package lib.cli.parameter;

import lib.stat.dirmult.EstimationParameter;

public interface HasDeletionParameter {

	EstimationParameter getDeletionEstimationParameter();
	void setDeletionParameter(EstimationParameter dirMultParameter);
	
}
