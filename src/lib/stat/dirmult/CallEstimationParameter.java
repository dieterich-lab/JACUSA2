package lib.stat.dirmult;

import lib.estimate.MinkaParameter;

public class CallEstimationParameter extends AbstractEstimationParameter {
	
	public static final double ESTIMATED_ERROR = 0.01;
	
	private double estimatedError;
	
	public CallEstimationParameter() {
		super(false, false, new MinkaParameter());
		estimatedError = ESTIMATED_ERROR;
	}

	public double getEstimatedError() {
		return estimatedError;
	}

	public void setEstimatedError(final double estError) {
		this.estimatedError = estError;
	}
	
}
