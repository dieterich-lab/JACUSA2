package jacusa.filter.basecall;

import java.util.List;

import jacusa.filter.AbstractDataFilter;
import jacusa.filter.cache.FilterCache;
import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.BaseCallCount;
import lib.data.ParallelData;
import lib.data.has.hasBaseCallCount;

public abstract class AbstractBaseCallDataFilter<T extends AbstractData & hasBaseCallCount> 
extends AbstractDataFilter<T> {

	private int minCount;
	private double minRatio;

	public AbstractBaseCallDataFilter(final char c, 
			final int overhang, 
			final int minCount, final double minRatio,
			final AbstractParameter<T, ?> parameter,
			final List<List<FilterCache<T>>> conditionFilterCaches) {
		
		super(c, overhang, parameter, conditionFilterCaches);

		this.minCount = minCount;
		this.minRatio = minRatio;
	}

	@Override
	protected boolean filter(final ParallelData<T> parallelData) {
		addFilteredData(parallelData);

		final int[] variantBaseIndexs = ParallelData.getVariantBaseIndexs(parallelData);

		for (int variantBaseIndex : variantBaseIndexs) {
			int count = 0;
			int filteredCount = 0;

			for (int conditionIndex = 0; conditionIndex < parallelData.getConditions(); ++conditionIndex) {
				for (int replicateIndex = 0; replicateIndex < parallelData.getReplicates(conditionIndex); replicateIndex++) {
					count += parallelData.getData(conditionIndex, replicateIndex).getBaseCallCount().getBaseCallCount(variantBaseIndex);
					filteredCount += getFilteredBaseCallData(parallelData, conditionIndex, replicateIndex).getBaseCallCount(variantBaseIndex);
				}
			}
			
			if (filter(count, filteredCount)) {
				return true;
			}
			
		}
		
		return false;
	}

	protected abstract BaseCallCount getFilteredBaseCallData(final ParallelData<T> parallelData, final int conditionIndex, final int replicateIndex);

	protected boolean filter(final int count, int filteredCount) {
		return (double)filteredCount / (double)count <= minRatio || count - filteredCount >= minCount;
	}
	
}
