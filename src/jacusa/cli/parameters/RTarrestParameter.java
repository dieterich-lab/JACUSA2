package jacusa.cli.parameters;

import jacusa.io.format.rtarrest.BED6rtArrestResultFormat;
import jacusa.method.rtarrest.BetaBinFactory;
import jacusa.method.rtarrest.RTarrestMethod;
import lib.cli.parameter.AbstractConditionParameter;
import lib.cli.parameter.AbstractParameter;
import lib.cli.parameter.JACUSAConditionParameter;

/**
 * Class defines parameters and default values that are need for Reverse Transcription arrest (rt-arrest).
 */
public class RTarrestParameter
extends AbstractParameter 
implements HasStatParameter {

	private StatParameter statParameter;

	public RTarrestParameter(final int conditions) {
		super(conditions);
		// related to test-statistic
		setStatParameter(new StatParameter(
				new BetaBinFactory(), Double.NaN));
		// default result format
		setResultFormat(
				new BED6rtArrestResultFormat(
				RTarrestMethod.Factory.NAME, 
				this));
	}

	@Override
	public AbstractConditionParameter createConditionParameter(final int conditionIndex) {
		final AbstractConditionParameter p = new JACUSAConditionParameter(conditionIndex);
		p.setMinBASQ((byte)0);
		return p;
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
