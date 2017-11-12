package jacusa.filter;

import jacusa.filter.counts.AbstractBaseCallCountFilter;
import jacusa.filter.counts.CombinedBaseCallCountFilter;
import jacusa.filter.factory.AbstractFilterFactory;
import lib.data.AbstractData;
import lib.data.ParallelData;
import lib.data.Result;
import lib.data.builder.ConditionContainer;
import lib.data.has.hasBaseCallCount;
import lib.data.has.hasReferenceBase;

public abstract class AbstractDistanceFilter<T extends AbstractData & hasReferenceBase & hasBaseCallCount, F extends AbstractData & hasBaseCallCount> 
extends AbstractFilter<T> {

	private final int filterDistance;
	private final AbstractBaseCallCountFilter<T, F> countFilter;
	
	public AbstractDistanceFilter(final char c, 
			final int filterDistance, final double minRatio, final int minCount,
			final AbstractFilterFactory<T, F> filterFactory) {
		super(c);
		this.filterDistance	= filterDistance;
		
		countFilter = new CombinedBaseCallCountFilter<T, F>(minRatio, minCount, filterFactory);
	}

	@Override
	protected boolean filter(final Result<T> result, final ConditionContainer<T> conditionContainer) {
		final ParallelData<T> parallelData = result.getParellelData();

		final int[] variantBaseIndexs = countFilter.getVariantBaseIndexs(parallelData);
		if (variantBaseIndexs.length == 0) {
			return false;
		}

		// get position from result
		// TODO final Coordinate coordinate = parallelData.getCoordinate();
		// TODO final FilterContainer<T> filterContainer = conditionContainer.getFilterContainer(coordinate);
		
		final ParallelData<F> parallelFilterData = null; // TODO
		
		return countFilter.filter(variantBaseIndexs, parallelData, parallelFilterData);
	}

	public int getFilterDistance() {
		return filterDistance;
	}
	
	@Override
	public int getOverhang() {
		return filterDistance;
	}
	
}
