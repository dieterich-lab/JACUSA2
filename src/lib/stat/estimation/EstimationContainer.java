package lib.stat.estimation;

import java.util.HashSet;
import java.util.Set;

public class EstimationContainer {

	private ConditionEstimate[] conditionEstimates;
	private ConditionEstimate pooledConditionEstimate;
	private Set<ConditionEstimate> estimates;
	
	public EstimationContainer(
			final ConditionEstimate[] conditionEstimates,
			final ConditionEstimate pooledConditionEstimate) {
		this.conditionEstimates 		= conditionEstimates;
		estimates 						= new HashSet<ConditionEstimate>();
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
	
	public void updateCondition(final int conditionIndex, final ConditionEstimate conditionEstimate) {
		if (conditionEstimates[conditionIndex].getIteration() == 0) {
			conditionEstimates[conditionIndex] = conditionEstimate;
		}
		final int otherConditionIndex = (conditionEstimates.length + 1) - (conditionIndex + 1);
		conditionEstimates[otherConditionIndex].clear();

		pooledConditionEstimate.clear();
	}

	public Set<ConditionEstimate> getEstimates() {
		return estimates;
	}
	
}
