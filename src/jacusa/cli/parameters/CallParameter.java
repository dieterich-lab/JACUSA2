package jacusa.cli.parameters;

import java.util.ArrayList;

import java.util.Arrays;
import java.util.List;

import jacusa.io.format.modifyresult.AddDeletionCount;
import jacusa.io.format.modifyresult.AddInsertionCount;
import jacusa.io.format.modifyresult.ResultModifier;
import lib.cli.parameter.GeneralParameter;
import lib.cli.parameter.HasDeletionParameter;
import lib.cli.parameter.HasInsertionParameter;
import lib.stat.DeletionStat;
import lib.stat.InsertionStat;
import lib.stat.dirmult.CallDirMultParameter;
import lib.stat.dirmult.DirMultParameter;

/**
 * Parameters specific to call method(s).
 */
public class CallParameter
extends GeneralParameter
implements HasStatParameter, HasInsertionParameter, HasDeletionParameter {
	
	private StatParameter statParameter;
	
	private final CallDirMultParameter callDirMultParameter;
	private DirMultParameter insertionDirMultParameter;
	private DirMultParameter deletionDirMultParameter;
	
	public CallParameter(final int conditionSize) {
		super(conditionSize);
		 
		this.callDirMultParameter 	= new CallDirMultParameter();
	}
	
	@Override
	public StatParameter getStatParameter() {
		return statParameter;
	}
	
	@Override
	public void setStatParameter(final StatParameter statParameter) {
		this.statParameter = statParameter;
	}
	
	public CallDirMultParameter getDirMultParameter() {
		return callDirMultParameter;
	}
	
	@Override
	public DirMultParameter getDeletionParameter() {
		return deletionDirMultParameter;
	}
	
	@Override
	public DirMultParameter getInsertionParameter() {
		return insertionDirMultParameter;
	}

	@Override
	public void setDeletionParameter(DirMultParameter dirMultParameter) {
		this.deletionDirMultParameter = dirMultParameter;
	}
	
	@Override
	public void setInsertionParameter(DirMultParameter dirMultParameter) {
		this.insertionDirMultParameter = dirMultParameter;
	}

	@Override
	public void registerKeys() {
		// INDEL counts
		if (showInsertionCount() || showInsertionStartCount()) {
			registerKey(InsertionStat.SCORE);
			registerKey(InsertionStat.PVALUE);
			getResultModifiers().add(new AddInsertionCount());
		}
		if (showDeletionCount()) {
			registerKey(DeletionStat.SCORE);
			registerKey(DeletionStat.PVALUE);
			getResultModifiers().add(new AddDeletionCount());
		}
		// resultModifier such as: add insertion_ratio
		for (final ResultModifier resultModifier : getResultModifiers()) {
			resultModifier.registerKeys(this);
		}
			
		// estimate call score
		registerKey("NumericallyInstable");
		if (callDirMultParameter.calcPValue()) {
			registerKey("pvalue");
		}
		if (callDirMultParameter.showAlpha()) {
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
		registerKey("score_estimation");
		
		// show subsampled scores
		if (callDirMultParameter.getSubsampleRuns() > 0) {
			registerKey("score_subsampled");
			if (showInsertionCount() || showInsertionStartCount()) {
				registerKey("insertion_score_subsampled");
			}
			if (showDeletionCount()) {
				registerKey("deletion_score_subsampled");
			}
		}
		
		// TODO add estimation details for INDELs
	}
	
}
