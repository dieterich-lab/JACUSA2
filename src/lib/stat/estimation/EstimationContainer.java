package lib.stat.estimation;

import java.util.ArrayList;
import java.util.List;

public class EstimationContainer {

	private ConditionEstimate[] conditionEstimates;
	private ConditionEstimate pooledConditionEstimate;
	private List<ConditionEstimate> estimates;
	
	public EstimationContainer(
			final ConditionEstimate[] conditionEstimates,
			final ConditionEstimate pooledConditionEstimate) {
		this.conditionEstimates 		= conditionEstimates;
		estimates 						= new ArrayList<ConditionEstimate>();
		for (final ConditionEstimate conditionEstimate : this.conditionEstimates) {
			estimates.add(conditionEstimate);
		}
		this.pooledConditionEstimate 	= pooledConditionEstimate;
		estimates.add(pooledConditionEstimate);
	}
	
	public ConditionEstimate[] getConditionEstimates() {
		return conditionEstimates;
	}
	
	public ConditionEstimate getConditionEstimate(final int conditionIndex) {
		return conditionEstimates[conditionIndex];
	}
	
	public ConditionEstimate getPooledEstimate() {
		return pooledConditionEstimate;
	}

	public boolean isNumericallyStable() {
		for (final ConditionEstimate conditionEstimate : conditionEstimates) {
			if (! conditionEstimate.isNumericallyStable()) {
				return false;
			}
		}

		return pooledConditionEstimate.isNumericallyStable();
	}
	
	public void updateCondition(
			final int pickedConditionIndex,
			final ConditionEstimate conditionEstimate,
			final int otherConditionIndex) {
		conditionEstimates[pickedConditionIndex] = new FastConditionEstimate(conditionEstimate);
		conditionEstimates[otherConditionIndex].clear();
		pooledConditionEstimate.clear();
	}

	public List<ConditionEstimate> getEstimates() {
		return estimates;
	}
	
}
