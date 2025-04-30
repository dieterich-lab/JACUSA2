package jacusa.cli.parameters;

import lib.cli.parameter.GeneralParameter;
import lib.cli.parameter.HasDeletionParameter;
import lib.cli.parameter.HasInsertionParameter;
import lib.stat.dirmult.DefaultEstimationParameter;
import lib.stat.dirmult.EstimationParameter;

/**
 * Class defines parameters and default values that are need for pileup method.
 */
public class PileupParameter extends GeneralParameter 
implements HasStatParameter, HasInsertionParameter, HasDeletionParameter {

	private StatParameter statParameter;
	private EstimationParameter insertionEstimationParameter;
	private EstimationParameter deletionEstimationParameter;
	
	public PileupParameter(final int conditions) {
		super(conditions);
		
		insertionEstimationParameter	= new DefaultEstimationParameter();
		deletionEstimationParameter		= new DefaultEstimationParameter();
	}

	@Override
	public EstimationParameter getDeletionEstimationParameter() {
		return deletionEstimationParameter;
	}
	
	@Override
	public EstimationParameter getInsertionEstimationParameter() {
		return insertionEstimationParameter;
	}

	@Override
	public void setDeletionParameter(EstimationParameter dirMultParameter) {
		this.deletionEstimationParameter = dirMultParameter;
	}
	
	@Override
	public void setInsertionParameter(EstimationParameter dirMultParameter) {
		this.insertionEstimationParameter = dirMultParameter;
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
		if (showInsertionCount() || showInsertionStartCount()) {
			addInsertionKeys(false, 0);
		}
		
		if (showDeletionCount()) {
			addDeletionKeys(false, 0);
		}
		
		super.registerKeys();
	}
}
