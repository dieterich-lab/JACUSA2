package lib.stat.betabin;

import lib.cli.parameter.GeneralParameter;
import lib.estimate.MinkaParameter;
import lib.stat.dirmult.AbstractDirMultParameter;

public class RTarrestBetaBinParameter extends AbstractDirMultParameter {

	public RTarrestBetaBinParameter(final GeneralParameter parameters) {
		super(parameters, false, true, new MinkaParameter());
	}
	
}
