package lib.stat.betabin;

import lib.cli.parameter.GeneralParameter;
import lib.estimate.MinkaParameter;
import lib.stat.dirmult.AbstractDirMultParameter;

public class LRTarrestBetaBinParameter extends AbstractDirMultParameter {

	public LRTarrestBetaBinParameter(final GeneralParameter parameters) {
		super(parameters, false, true, new MinkaParameter());
	}
	
}
