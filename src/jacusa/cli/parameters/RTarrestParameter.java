package jacusa.cli.parameters;

import jacusa.io.format.rtarrest.BED6rtArrestResultFormat;
import jacusa.method.rtarrest.RTarrestMethod;
import lib.cli.parameter.ConditionParameter;
import lib.cli.parameter.GeneralParameter;
import lib.stat.betabin.RTarrestStatFactory;

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
		
		// related to test-statistic
		setStatParameter(
				new StatParameter(new RTarrestStatFactory(), Double.NaN));
		// default result format
		setResultFormat(
				new BED6rtArrestResultFormat(RTarrestMethod.Factory.NAME, this));
	}

	@Override
	public ConditionParameter createConditionParameter(final int condI) {
		final ConditionParameter p = super.createConditionParameter(condI);
		// TODO how to set minBASQ - this can have downstream effects on how a read is defined
		// Pos.:		1234
		// Read Seq.:	ACGT
		// Read Basq:	HHHL H=High Quality BC, L=Quality BC
		// Where does the Read end position 3 or 4?
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
