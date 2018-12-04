package jacusa.cli.parameters;

import jacusa.io.format.lrtarrest.BED6lrtArrestResultFormat;
import jacusa.method.lrtarrest.LRTarrestMethod;
import jacusa.method.lrtarrest.LRTarrestStatFactory;
import lib.cli.parameter.AbstractConditionParameter;
import lib.cli.parameter.AbstractParameter;
import lib.cli.parameter.JACUSAConditionParameter;

/**
 * Class defines parameters and default values that are need for Linked Reverse Transcription arrest (lrt-arrest).
 */
public class LRTarrestParameter
extends AbstractParameter
implements HasStatParameter {

	private StatParameter statParameter;

	public LRTarrestParameter(final int conditions) {
		super(conditions);
		// change window size
		setActiveWindowSize(500);
		
		// test-statistic related
		setStatParameter(
				new StatParameter(new LRTarrestStatFactory(), 1.0));
		// default output format
		setResultFormat(
				new BED6lrtArrestResultFormat(
						LRTarrestMethod.Factory.NAME, this));

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
