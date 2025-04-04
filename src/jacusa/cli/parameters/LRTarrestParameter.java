package jacusa.cli.parameters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jacusa.io.format.modifyresult.ResultModifier;
import jacusa.worker.LRTarrestWorker;
import lib.cli.parameter.GeneralParameter;
import lib.stat.DeletionStat;
import lib.stat.InsertionStat;
import lib.stat.betabin.LRTarrestBetaBinParameter;

/**
 * Class defines parameters and default values that are need for Linked Reverse 
 * Transcription arrest (lrt-arrest).
 */
public class LRTarrestParameter extends GeneralParameter 
implements HasStatParameter {

	private StatParameter statParameter;

	private LRTarrestBetaBinParameter betaBinParameter;
	
	public LRTarrestParameter(final int conditions) {
		super(conditions);
		// change defaults
		
		// reduce window size to save memory consumption
		// storing linkage between rt sites and mismatches is memory intensive
		setActiveWindowSize(5000);
	}

	public LRTarrestBetaBinParameter getLRTarrestBetaBinParameter() {
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
		registerKey(LRTarrestWorker.INFO_VARIANT_SCORE);
		
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
