package jacusa.filter.basecall;

import jacusa.filter.FilterRatio;
import lib.data.AbstractData;
import lib.data.BaseCallCount;
import lib.data.BaseCallData;
import lib.data.ParallelData;
import lib.data.has.HasBaseCallCount;
import lib.data.has.HasReferenceBase;

public class BaseCallCountFilter<T extends AbstractData & HasBaseCallCount & HasReferenceBase> {

	private final FilterRatio filterRatio;
	
	public BaseCallCountFilter(final FilterRatio filterRatio) {
		this.filterRatio = filterRatio;
	}

	public boolean filter(final ParallelData<T> parallelData, final ParallelData<BaseCallData> filteredParallelData) {
		final int[] variantBaseIndexs = ParallelData.getVariantBaseIndexs(parallelData);

		for (int variantBaseIndex : variantBaseIndexs) {
			int count = 0;
			int filteredCount = 0;

			for (int conditionIndex = 0; conditionIndex < parallelData.getConditions(); ++conditionIndex) {
				final int replicates = parallelData.getReplicates(conditionIndex);
				for (int replicateIndex = 0; replicateIndex < replicates; replicateIndex++) {
					// observed count
					final T data = parallelData.getData(conditionIndex, replicateIndex);
					final int tmpCount = data.getBaseCallCount().getBaseCallCount(variantBaseIndex);
					count += tmpCount;
					// possible artefact count
					final BaseCallCount filteredBaseCallCount = 
							filteredParallelData.getData(conditionIndex, replicateIndex).getBaseCallCount();
					if (filteredBaseCallCount != null) {
						filteredCount += tmpCount - filteredBaseCallCount.getBaseCallCount(variantBaseIndex);						
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
