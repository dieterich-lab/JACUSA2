package lib.stat.betabin;

import lib.estimate.MinkaParameter;
import lib.stat.dirmult.AbstractEstimationParameter;

public class LRTarrestBetaBinParameter extends AbstractEstimationParameter {

	public LRTarrestBetaBinParameter() {
		super(false, true, new MinkaParameter());
	}
	
}
