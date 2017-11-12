package jacusa.filter.counts;

import jacusa.filter.factory.AbstractFilterFactory;
import lib.data.AbstractData;
import lib.data.ParallelData;
import lib.data.has.hasBaseCallCount;
import lib.data.has.hasReferenceBase;

public class CombinedBaseCallCountFilter<T extends AbstractData & hasReferenceBase & hasBaseCallCount, F extends AbstractData & hasBaseCallCount> 
extends AbstractBaseCallCountFilter<T, F> {

	private RatioCountFilter<T, F> minRatioFilter;
	private MinCountFilter<T, F> minCountFilter;
	
	public CombinedBaseCallCountFilter(
			final double minRatio, final double minCount, 
			final AbstractFilterFactory<T, F> filterFactory) {
		super(filterFactory);
		
		minRatioFilter = new RatioCountFilter<T, F>(minRatio, filterFactory);
		minCountFilter = new MinCountFilter<T, F>(minRatio, filterFactory);
	}

	@Override
	protected boolean filter(final int variantBaseIndex, 
			final ParallelData<T> parallelData, 
			final ParallelData<F> parallelStorageFilterData) {
		return minCountFilter.filter(variantBaseIndex, parallelData, parallelStorageFilterData) &&
				minRatioFilter.filter(variantBaseIndex, parallelData, parallelStorageFilterData);
	}

	
}