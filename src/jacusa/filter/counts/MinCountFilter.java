package jacusa.filter.counts;

import jacusa.filter.factory.AbstractFilterFactory;
import lib.data.AbstractData;
import lib.data.ParallelData;

import lib.data.has.hasBaseCallCount;
import lib.data.has.hasReferenceBase;

public class MinCountFilter<T extends AbstractData & hasBaseCallCount & hasReferenceBase, F extends AbstractData & hasBaseCallCount> 
extends AbstractBaseCallCountFilter<T, F> {

	private double minCount;
	
	public MinCountFilter( 
			final double minCount, 
			final AbstractFilterFactory<T, F> filterFactory) {

		super(filterFactory);
		this.minCount = minCount;
	}

	@Override
	protected boolean filter(final int variantBaseIndex, 
			final ParallelData<T> parallelData, 
			final ParallelData<F> parallelFilterData) {
		int count = parallelData
				.getCombinedPooledData()
				.getBaseCallCount()
				.getBaseCallCount(variantBaseIndex);
		if (count == 0) {
			return false;
		}

		ParallelData<F> filteredParallelData = applyFilter(variantBaseIndex, parallelData, parallelFilterData);
		int filteredCount = filteredParallelData
				.getCombinedPooledData()
				.getBaseCallCount()
				.getBaseCallCount(variantBaseIndex);

		return count - filteredCount >= minCount;
	}

	public double getMinCount() {
		return minCount;
	}
	
}
