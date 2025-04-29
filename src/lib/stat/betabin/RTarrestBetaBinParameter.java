package lib.stat.betabin;

import lib.estimate.MinkaParameter;
import lib.stat.dirmult.AbstractEstimationParameter;

public class RTarrestBetaBinParameter extends AbstractEstimationParameter {

	public RTarrestBetaBinParameter() {
		super(false, true, new MinkaParameter());
	}
	
}
