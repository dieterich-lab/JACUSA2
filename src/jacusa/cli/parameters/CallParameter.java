package jacusa.cli.parameters;

import jacusa.io.format.call.BED6callResultFormat;
import jacusa.method.call.CallMethod;
import lib.cli.parameter.AbstractConditionParameter;
import lib.cli.parameter.AbstractParameter;
import lib.cli.parameter.JACUSAConditionParameter;
import lib.stat.dirmult.DirMultRobustCompoundErrorFactory;

/**
 * Parameters specific to call method(s).
 */
public class CallParameter 
extends AbstractParameter
implements HasStatParameter {
	
	private StatParameter statParameter;
	
	public CallParameter(final int conditionSize) {
		super(conditionSize);
		
		// set defaults
		setResultFormat(
				new BED6callResultFormat(
						CallMethod.Factory.NAME_PREFIX + conditionSize, 
						this));
		setStatParameter(
				new StatParameter(
						new DirMultRobustCompoundErrorFactory(
								getResultFormat()),
						Double.NaN));
	}
	
	@Override
	public AbstractConditionParameter createConditionParameter(final int conditionIndex) {
		return new JACUSAConditionParameter(conditionIndex);
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
