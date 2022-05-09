package lib.stat.dirmult;

import lib.estimate.MinkaParameter;

class CallDirMultParameter extends AbstractDirMultParameter {

	public CallDirMultParameter() {
		super(false, false, new MinkaParameter(), DirMultParameter.ESTIMATED_ERROR);
	}

}
