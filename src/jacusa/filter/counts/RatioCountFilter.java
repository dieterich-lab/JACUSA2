package jacusa.filter.counts;

import jacusa.filter.factory.AbstractFilterFactory;
import lib.data.AbstractData;
import lib.data.ParallelData;
import lib.data.has.hasBaseCallCount;
import lib.data.has.hasReferenceBase;

public class RatioCountFilter<T extends AbstractData & hasReferenceBase & hasBaseCallCount, F extends AbstractData & hasBaseCallCount> 
extends AbstractBaseCallCountFilter<T,F> {

	private double minRatio;

	public RatioCountFilter(final double minRatio, final AbstractFilterFactory<T, F> filterFactory) {
		super(filterFactory);
		this.minRatio = minRatio;
	}

	@Override
	protected boolean filter(final int variantBaseIndex, 
			final ParallelData<T> parallelData,
			final ParallelData<F> parallelStoreageFilterData) {
		
		final int count = parallelData
				.getCombinedPooledData()
				.getBaseCallCount()
				.getBaseCallCount(variantBaseIndex);
		
		final ParallelData<F> filteredParallelData = 
				applyFilter(variantBaseIndex, parallelData, parallelStoreageFilterData);

		final int filteredCount = filteredParallelData
				.getCombinedPooledData()
				.getBaseCallCount()
				.getBaseCallCount(variantBaseIndex);

		return (double)filteredCount / (double)count <= minRatio;
	}

	public double getMinRatio() {
		return minRatio;
	}

}