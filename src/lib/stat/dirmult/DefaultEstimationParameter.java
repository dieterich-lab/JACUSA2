package lib.stat.dirmult;

import lib.estimate.MinkaParameter;

public class DefaultEstimationParameter extends AbstractEstimationParameter {
	
	public DefaultEstimationParameter() {
		super(false, false, new MinkaParameter());
	}
	
}
