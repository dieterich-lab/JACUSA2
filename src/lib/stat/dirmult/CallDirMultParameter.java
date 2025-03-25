package lib.stat.dirmult;

import lib.cli.parameter.GeneralParameter;
import lib.estimate.MinkaParameter;

public class CallDirMultParameter extends AbstractDirMultParameter {
	
	public static final double ESTIMATED_ERROR = 0.01;
	
	private double estimatedError;
	
	public CallDirMultParameter(final GeneralParameter parameters) {
		super(parameters, false, false, new MinkaParameter());
		estimatedError = ESTIMATED_ERROR;
	}

	public double getEstimatedError() {
		return estimatedError;
	}

	public void setEstimatedError(final double estError) {
		this.estimatedError = estError;
	}
	
}
