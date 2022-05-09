package lib.stat.betabin;

import lib.estimate.MinkaParameter;
import lib.stat.dirmult.AbstractDirMultParameter;

public class RTarrestBetaBinParameter extends AbstractDirMultParameter {

	public RTarrestBetaBinParameter() {
		super(false, true, new MinkaParameter(), 0.0d);
	}
	
}
