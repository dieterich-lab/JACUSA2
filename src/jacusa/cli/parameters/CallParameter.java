package jacusa.cli.parameters;

import lib.cli.parameter.GeneralParameter;

/**
 * Parameters specific to call method(s).
 */
public class CallParameter extends GeneralParameter implements HasStatParameter {
	
	private StatParameter statParameter;
	
	public CallParameter(final int conditionSize) {
		super(conditionSize);
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
