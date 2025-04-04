package jacusa.cli.parameters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jacusa.io.format.modifyresult.ResultModifier;
import lib.cli.parameter.ConditionParameter;
import lib.cli.parameter.GeneralParameter;
import lib.stat.DeletionStat;
import lib.stat.InsertionStat;
import lib.stat.betabin.RTarrestBetaBinParameter;
import lib.stat.betabin.RTarrestStat;


/**
 * Class defines parameters and default values that are need for Reverse 
 * Transcription arrest (rt-arrest).
 */
public class RTarrestParameter
extends GeneralParameter 
implements HasStatParameter {

	private StatParameter statParameter;
	private RTarrestBetaBinParameter betaBinParameter;
	
	public RTarrestParameter(final int conditions) {
		super(conditions);
		// change default values
		
		betaBinParameter = new RTarrestBetaBinParameter();
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
		
		// INDEL counts
		if (showInsertionCount() || showInsertionStartCount()) {
			registerConditionReplictaKeys("insertions");
			registerKey(InsertionStat.SCORE);
			registerKey(InsertionStat.PVALUE);
		}
		if (showDeletionCount()) {
			registerConditionReplictaKeys("deletions");
			registerKey(DeletionStat.SCORE);
			registerKey(DeletionStat.PVALUE);
		}
		// resultModifier such as: add insertion_ratio
		for (final ResultModifier resultModifier : getResultModifiers()) {
			resultModifier.registerKeys(this);
		}
			
		// estimate call score
		registerKey("NumericallyInstable");
		if (betaBinParameter.calcPValue()) {
			registerKey("pvalue");
		}
		if (betaBinParameter.showAlpha()) {
			List<String> ids = new ArrayList<String>();
			for (int conditionIndex = 0; conditionIndex < getConditionsSize(); conditionIndex++) {
			    ids.add(Integer.toString(conditionIndex + 1));
			}
			ids.add("P");
			for (String key : Arrays.asList("initAlpha", "alpha", "iteration", "logLikelihood", "reset", "backtrack")) {
				for (final String id : ids) {
					registerKey(key + id);
				}
			}
		}
		registerKey("estimation");
		
		// show subsampled scores
		if (betaBinParameter.getSubsampleRuns() > 0) {
			registerKey("score_subsampled");
			if (showInsertionCount() || showInsertionStartCount()) {
				registerConditionReplictaKeys("insertion_score_subsampled");
			}
			if (showDeletionCount()) {
				registerConditionReplictaKeys("insertion_score_subsampled");
			}
		}
		
		// TODO add estimation details for INDELs
	}
	
}
