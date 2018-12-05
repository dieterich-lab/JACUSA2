package jacusa.cli.parameters;

import jacusa.io.format.rtarrest.BED6rtArrestResultFormat;
import jacusa.method.rtarrest.BetaBinFactory;
import jacusa.method.rtarrest.RTarrestMethod;
import lib.cli.parameter.ConditionParameter;
import lib.cli.parameter.GeneralParameter;

/**
 * Class defines parameters and default values that are need for Reverse Transcription arrest (rt-arrest).
 */
public class RTarrestParameter
extends GeneralParameter 
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
	public ConditionParameter createConditionParameter(final int conditionIndex) {
		final ConditionParameter p = super.createConditionParameter(conditionIndex);
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
