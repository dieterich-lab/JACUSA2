package jacusa.cli.parameters;

import lib.cli.parameter.ConditionParameter;
import lib.cli.parameter.GeneralParameter;


/**
 * Class defines parameters and default values that are need for Reverse 
 * Transcription arrest (rt-arrest).
 */
public class RTarrestParameter
extends GeneralParameter 
implements HasStatParameter {

	private StatParameter statParameter;

	public RTarrestParameter(final int conditions) {
		super(conditions);
		// change default values
	}

	@Override
	public ConditionParameter createConditionParameter(final int conditionIndex) {
		final ConditionParameter p = super.createConditionParameter(conditionIndex);
		// FIXME how to set minBASQ - this can have downstream effects on how a read is defined
		// Pos.:		1234
		// Read Seq.:	ACGT
		// Read Basq:	HHHL H=High Quality BC, L=Quality BC
		// Where does the Read end? Position 3 or 4?
		// p.setMinBASQ((byte)0); 
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
