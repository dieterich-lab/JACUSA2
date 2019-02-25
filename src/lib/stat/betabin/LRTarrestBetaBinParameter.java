package lib.stat.betabin;

import lib.estimate.MinkaParameter;
import lib.stat.dirmult.AbstractDirMultParameter;

public class LRTarrestBetaBinParameter extends AbstractDirMultParameter {

	public LRTarrestBetaBinParameter() {
		super(false, true, new MinkaParameter(), Double.NaN);
	}
	
}
