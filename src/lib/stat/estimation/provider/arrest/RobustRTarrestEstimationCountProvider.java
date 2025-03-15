package lib.stat.estimation.provider.arrest;

import java.util.Arrays;
import java.util.List;

import lib.data.ParallelData;

public class RobustRTarrestEstimationCountProvider extends AbstractRTarrestEstimationCountProvider {

	public RobustRTarrestEstimationCountProvider(final int maxIterations) {
		super(maxIterations, 1d);
	}

	@Override
	protected List<List<Count>> process(final ParallelData parallelData) {

		final Count count1 = new Count(parallelData.getPooledData(0));
		final Count count2 = new Count(parallelData.getPooledData(1));
 
		final boolean both1 = count1.both();
		final boolean both2 = count2.both();

		final List<List<Count>> orginalCounts = getCounts(parallelData);
		if (both1 && both2) {
			return orginalCounts;
		}
		
		int keepConditionIndex 		= -1;
		int changeConditionIndex 	= -1;
		
		// determine which condition has the variant base
		if (both1 && ! both2) { // condition1
			keepConditionIndex 		= 0;
			changeConditionIndex 	= 1;
		} else if (both2 && ! both1) { // condition2
			keepConditionIndex 		= 1;
			changeConditionIndex 	= 0;
		}
		
		if (keepConditionIndex >= 0 && 
				changeConditionIndex >= 0) {
			READ_INDEX exclusiveIndex = READ_INDEX.THROUGH;
			if (count1.through > 0 && count2.through > 0) {
				exclusiveIndex = READ_INDEX.ARREST;
			}

			// container for adjusted parallelPileup
			final List<List<Count>> modifiedCounts = Arrays.asList(null, null);
			modifiedCounts.set(keepConditionIndex, orginalCounts.get(keepConditionIndex));
			modifiedCounts.set(
					changeConditionIndex, 
					flat(orginalCounts.get(keepConditionIndex), exclusiveIndex));
			return modifiedCounts;
		}
		
		// aP > 3, just use the existing parallelPileup to calculate the test-statistic		
		return orginalCounts;
	}

}
