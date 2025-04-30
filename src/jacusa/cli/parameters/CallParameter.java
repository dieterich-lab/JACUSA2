package jacusa.cli.parameters;

import lib.cli.parameter.GeneralParameter;
import lib.cli.parameter.HasDeletionParameter;
import lib.cli.parameter.HasInsertionParameter;
import lib.stat.dirmult.CallEstimationParameter;
import lib.stat.dirmult.DefaultEstimationParameter;
import lib.stat.dirmult.EstimationParameter;

/**
 * FIXME old format
 */

/**
 * Parameters specific to call method(s).
 */
public class CallParameter
extends GeneralParameter
implements HasStatParameter, HasInsertionParameter, HasDeletionParameter {
	
	private StatParameter statParameter;
	
	private final CallEstimationParameter callEstimationParameter;
	private EstimationParameter insertionEstimationParameter;
	private EstimationParameter deletionEstimationParameter;
	
	public CallParameter(final int conditionSize) {
		super(conditionSize);
		callEstimationParameter 		= new CallEstimationParameter();
		insertionEstimationParameter	= new DefaultEstimationParameter();
		deletionEstimationParameter		= new DefaultEstimationParameter();
	}
	
	@Override
	public StatParameter getStatParameter() {
		return statParameter;
	}
	
	@Override
	public void setStatParameter(final StatParameter statParameter) {
		this.statParameter = statParameter;
	}
	
	public CallEstimationParameter getCallEstimationarameter() {
		return callEstimationParameter;
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
	public void registerKeys() {
		addCallKeys(callEstimationParameter.calcPValue(), callEstimationParameter.showAlpha(), callEstimationParameter.getSubsampleRuns());
		
		if (showInsertionCount() || showInsertionStartCount()) {
			addInsertionKeys(insertionEstimationParameter.showAlpha(), callEstimationParameter.getSubsampleRuns());
		}
		
		if (showDeletionCount()) {
			addDeletionKeys(deletionEstimationParameter.showAlpha(), callEstimationParameter.getSubsampleRuns());
		}
		
		super.registerKeys();
	}
	
	
	
}