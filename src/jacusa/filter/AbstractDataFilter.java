package jacusa.filter;

import java.util.List;

import jacusa.filter.cache.FilterCache;
import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.ParallelData;
import lib.data.result.Result;
import lib.util.coordinate.Coordinate;

/**
 * Defines a class that needs additional data to be calculate to filter artefacts.
 * 
 * @param <T>
 */
public abstract class AbstractDataFilter<T extends AbstractData> 
extends AbstractFilter<T> {

	// first list stores condition and each nested list the respective replicates
	protected final List<List<FilterCache<T>>> filterCaches;

	protected AbstractDataFilter(final char c, 
			final int overhang, 
			final AbstractParameter<T, ?> parameter,
			final List<List<FilterCache<T>>> conditionFilterCaches) {

		super(c, overhang);
		this.filterCaches = conditionFilterCaches;
	}

	@Override
	public boolean applyFilter(final Result<T> result) {
		// add filtered data and then filter
		addFilteredData(result.getParellelData());
		return super.applyFilter(result);
	}
	
	/**
	 * FilterCache data that is linked by coordinate is added to parallelData.
	 * 
	 * @param parallelData the parallelData to add filterCache data
	 */
	private void addFilteredData(final ParallelData<T> parallelData) {
		final int conditions = parallelData.getConditions();
		// coordinate of parallelData - corresponds to linked data in filterCache
		final Coordinate coordinate = parallelData.getCoordinate();

		for (int conditionIndex = 0; conditionIndex < conditions; ++conditionIndex) {
			// number of replicates for condition
			final int replicates = parallelData.getReplicates(conditionIndex);

			// add data from filterCache(conditionIndex, replicateIndex) 
			// to ParallelData(conditionIndex, replicateIndex)
			for (int replicateIndex = 0; replicateIndex < replicates; replicateIndex++) {
				// data from parallelData
				final T data = parallelData.getData(conditionIndex, replicateIndex);
				// data from filterFache
				final FilterCache<T> filterCache = getFilterCache(conditionIndex, replicateIndex);
				filterCache.addData(data, coordinate); 
			}
		}
	}

	/**
	 * Helper function. Clears conditionFilterFaches.
	 */
	public void clear() {
		for (List<FilterCache<T>> replicateFilterCaches : filterCaches) {
			for (FilterCache<T> replicateFilter : replicateFilterCaches) {
				replicateFilter.clear();
			}
		}
	}

	/**
	 * Returns the filterCache for this filter for a specific condition and replicate.
	 * 
	 * @param conditionIndex identifies a specific condition
	 * @param replicateIndex identifies a specific replicate
	 * @return a specific filterCache
	 */
	public FilterCache<T> getFilterCache(final int conditionIndex, final int replicateIndex) {
		return filterCaches.get(conditionIndex).get(replicateIndex);
	}

}
