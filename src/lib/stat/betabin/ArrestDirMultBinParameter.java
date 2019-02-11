package lib.stat.betabin;

import lib.estimate.MinkaParameter;
import lib.stat.dirmult.AbstractDirMultParameter;

public class ArrestDirMultBinParameter extends AbstractDirMultParameter {

	public ArrestDirMultBinParameter() {
		super(false, true, new MinkaParameter(), Double.NaN);
	}
	
}
