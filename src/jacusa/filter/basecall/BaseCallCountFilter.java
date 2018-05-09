package jacusa.filter.basecall;

import jacusa.filter.FilterRatio;
import lib.data.count.BaseCallCount;

public class BaseCallCountFilter {

	private final FilterRatio filterRatio;
	
	public BaseCallCountFilter(final FilterRatio filterRatio) {
		this.filterRatio = filterRatio;
	}

	public boolean filter(final int[] variantBaseIndexs, 
			final BaseCallCount[][] observed, final BaseCallCount[][] filtered) {

		for (int variantBaseIndex : variantBaseIndexs) {
			int count = 0;
			int filteredCount = 0;

			for (int conditionIndex = 0; conditionIndex < observed.length; ++conditionIndex) {
				final int replicates = observed[conditionIndex].length;
				for (int replicateIndex = 0; replicateIndex < replicates; replicateIndex++) {
					// observed count
					final BaseCallCount o = observed[conditionIndex][replicateIndex];
					final int tmpCount = o.getBaseCall(variantBaseIndex);
					count += tmpCount;
					// possible artefact count
					final BaseCallCount f = filtered[conditionIndex][replicateIndex];
					if (f != null) {
						filteredCount += tmpCount - f.getBaseCall(variantBaseIndex);						
					} else {
						filteredCount += tmpCount;
					}
				}
			}

			// check if too much filteredCount
			if (filterRatio.filter(count, filteredCount)) {
				return true;
			}
			
		}

		return false;

	}
	
}
