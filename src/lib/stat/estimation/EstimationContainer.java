package lib.stat.estimation;

public class EstimationContainer {

	private ConditionEstimate[] conditionEstimates;
	private ConditionEstimate pooledConditionEstimate;
	
	public EstimationContainer(
			final ConditionEstimate[] conditionEstimates,
			final ConditionEstimate pooledConditionEstimate) {
		this.conditionEstimates 		= conditionEstimates;
		this.pooledConditionEstimate 	= pooledConditionEstimate;
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
	
}
