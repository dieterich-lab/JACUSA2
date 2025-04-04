package jacusa.cli.parameters;

import jacusa.io.format.modifyresult.ResultModifier;
import lib.cli.parameter.GeneralParameter;
import lib.stat.DeletionStat;
import lib.stat.InsertionStat;

/**
 * Class defines parameters and default values that are need for pileup method.
 */
public class PileupParameter extends GeneralParameter 
implements HasStatParameter {

	private StatParameter statParameter;
	
	public PileupParameter(final int conditions) {
		super(conditions);
		// change default values
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
			
		// TODO add estimation details for INDELs
	}
}
