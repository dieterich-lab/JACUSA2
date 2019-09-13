package lib.stat.dirmult;

import lib.estimate.MinkaParameter;

class CallDirMultParameter extends AbstractDirMultParameter {
	
	public static final double ESTIMATED_ERROR = 0.01;
	
	private double estError;
	
	public CallDirMultParameter() {
		super(false, false, new MinkaParameter());
		estError = ESTIMATED_ERROR;
	}

	public double getEstimatedError() {
		return estError;
	}

	public void setEstimatedError(final double estError) {
		this.estError = estError;
	}
	
}
