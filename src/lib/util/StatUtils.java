package lib.util;

import lib.data.ParallelData;

public abstract class StatUtils {

	public static int[] pickCondition(final ParallelData parallelData) {
		int pickedConditionIndex = 0;
		int pickedConditionCoverage = parallelData.getPooledData(pickedConditionIndex).getPileupCount().getReads();
		final int conditions = parallelData.getConditions();
	
		for (int conditionIndex = 1; conditionIndex < conditions; conditionIndex++) {
			final int coverage = parallelData.getPooledData(conditionIndex).getPileupCount().getReads();
			if (pickedConditionCoverage < coverage) {
				pickedConditionIndex = conditionIndex;
				pickedConditionCoverage = coverage;
			}
		}
		int otherConditionIndex = -1;
		if (pickedConditionIndex == 1) {
			otherConditionIndex = 0;
		} else {
			otherConditionIndex = 1;
		}
		
		return new int [] {pickedConditionIndex, otherConditionIndex};
	}
	
}
