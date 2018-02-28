package jacusa.filter;

import java.util.List;

import jacusa.filter.cache.FilterCache;
import jacusa.filter.factory.AbstractDataFilterFactory;
import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.ParallelData;
import lib.data.has.hasBaseCallCount;
import lib.data.has.hasLibraryType.LIBRARY_TYPE;
import lib.util.coordinate.Coordinate;

public class BaseCallDataFilter<T extends AbstractData & hasBaseCallCount, F extends AbstractData & hasBaseCallCount> 
extends AbstractDataFilter<T, F> {

	private int minCount;
	private double minRatio;
	
	public BaseCallDataFilter(final char c, 
			final int overhang, 
			final int minCount,
			final double minRatio,
			final AbstractParameter<T, ?> parameter,
			final AbstractDataFilterFactory<T, F> filterFactory,
			final List<List<FilterCache<F>>> conditionFilterCaches) {
		
		super(c, overhang, parameter, filterFactory, conditionFilterCaches);
		this.minCount = minCount;
		this.minRatio = minRatio;
	}

	@Override
	protected boolean filter(final ParallelData<T> parallelData) {
		final Coordinate coordinate = parallelData.getCoordinate();
		final int[] variantBaseIndexs = ParallelData.getVariantBaseIndexs(parallelData);
		
		for (int variantBaseIndex : variantBaseIndexs) {
			int count = 0;
			int filteredCount = 0;

			for (int conditionIndex = 0; conditionIndex < parallelData.getConditions(); ++conditionIndex) {
				for (int replicateIndex = 0; replicateIndex < parallelData.getReplicates(conditionIndex); replicateIndex++) {
					count += parallelData.getData(conditionIndex, replicateIndex).getBaseCallCount().getBaseCallCount(variantBaseIndex);

					final LIBRARY_TYPE libraryFype = parallelData.getData(conditionIndex, replicateIndex).getLibraryType();
					filteredCount += getFilteredData(coordinate, libraryFype, conditionIndex, replicateIndex).getBaseCallCount().getBaseCallCount(variantBaseIndex);
				}
			}
			
			if (filter(count, filteredCount)) {
				return true;
			}
			
		}
		
		return false;
	}

	protected boolean filter(final int count, int filteredCount) {
		return (double)filteredCount / (double)count <= minRatio || count - filteredCount >= minCount;
	}
	
}
