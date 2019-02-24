package lib.stat.dirmult;

import lib.estimate.MinkaParameter;

public class CallDirMultParameter extends AbstractDirMultParameter {

	public final static double ESTIMATED_ERROR = 0.01;
	
	private double estimatedError;
	
	public CallDirMultParameter() {
		super(false, false, new MinkaParameter(), Double.NaN);
		estimatedError = ESTIMATED_ERROR;
	}

	public double getEstimatedError() {
		return estimatedError;
	}

	public void setEstimatedError(final double estimatedError) {
		this.estimatedError = estimatedError;
	}
	
}
