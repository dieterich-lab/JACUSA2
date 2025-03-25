package jacusa.cli.parameters;

import lib.cli.parameter.GeneralParameter;

/**
 * Class defines parameters and default values that are need for pileup method.
 */
public class PileupParameter extends GeneralParameter 
implements HasStatParameter {

	private StatParameter statParameter;
	
	public PileupParameter(final int conditions) {
		super(conditions);
		// change default values
	}

	@Override
	public StatParameter getStatParameter() {
		return statParameter;
	}

	@Override
	public void setStatParameter(final StatParameter statParameter) {
		this.statParameter = statParameter;
		
	}

	
}
