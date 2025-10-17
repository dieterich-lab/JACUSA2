package jacusa.cli.parameters;

import lib.cli.parameter.ConditionParameter;
import lib.cli.parameter.GeneralParameter;
import lib.cli.parameter.HasDeletionParameter;
import lib.cli.parameter.HasInsertionParameter;
import lib.stat.betabin.RTarrestBetaBinParameter;
import lib.stat.betabin.RTarrestStat;
import lib.stat.dirmult.DefaultEstimationParameter;
import lib.stat.dirmult.EstimationParameter;


/**
 * Class defines parameters and default values that are need for Reverse 
 * Transcription arrest (rt-arrest).
 */
public class RTarrestParameter
extends GeneralParameter 
implements HasStatParameter, HasInsertionParameter, HasDeletionParameter {

	private StatParameter statParameter;
	private RTarrestBetaBinParameter betaBinParameter;
	private EstimationParameter insertionEstimationParameter;
	private EstimationParameter deletionEstimationParameter;
	
	public RTarrestParameter(final int conditions) {
		super(conditions);
		// change default values
		
		betaBinParameter 				= new RTarrestBetaBinParameter();
		insertionEstimationParameter	= new DefaultEstimationParameter();
		deletionEstimationParameter		= new DefaultEstimationParameter();
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
	public void setDeletionParameter(EstimationParameter estimationParameter) {
		this.deletionEstimationParameter = estimationParameter;
	}
	
	@Override
	public void setInsertionParameter(EstimationParameter estimationParameter) {
		this.insertionEstimationParameter = estimationParameter;
	}
	
	@Override
	public EstimationParameter getDeletionEstimationParameter() {
		return deletionEstimationParameter;
	}
	
	@Override
	public EstimationParameter getInsertionEstimationParameter() {
		return insertionEstimationParameter;
	}
	
	public RTarrestBetaBinParameter getBetaBinParameter() {
		return betaBinParameter;
	}
	
	@Override
	public StatParameter getStatParameter() {
		return statParameter;
	}

	@Override
	public void setStatParameter(final StatParameter statParameter) {
		this.statParameter = statParameter;
		
	}
	
	@Override
	public void registerKeys() {
		registerKey(RTarrestStat.ARREST_SCORE);
		// TODO test pvalue vs score
		addCallKeys(betaBinParameter.showAlpha(), betaBinParameter.showAlpha(), betaBinParameter.getSubsampleRuns());
		
		if (showInsertionCount() || showInsertionStartCount()) {
			addInsertionKeys(insertionEstimationParameter.showAlpha(), betaBinParameter.getSubsampleRuns());
		}
		
		if (showDeletionCount()) {
			addDeletionKeys(deletionEstimationParameter.showAlpha(), betaBinParameter.getSubsampleRuns());
		}
		
		super.registerKeys();
	}
	
}
