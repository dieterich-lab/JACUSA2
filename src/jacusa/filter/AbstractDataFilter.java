package jacusa.filter;

import java.util.List;

import jacusa.filter.cache.FilterCache;
import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.ParallelData;
import lib.util.coordinate.Coordinate;

/**
 * Defines a class that needs additional data to be calculate to filter artefacts.
 */
public abstract class AbstractDataFilter<T extends AbstractData> 
extends AbstractFilter<T> {

	// first list stores condition and each nested list the respective replicates
	private final List<List<FilterCache<T>>> filterCaches;
	
	protected AbstractDataFilter(final char c, 
			final int overhang, 
			final AbstractParameter<T, ?> parameter,
			final List<List<FilterCache<T>>> conditionFilterCaches) {

		super(c, overhang);
		this.filterCaches = conditionFilterCaches;
	}

	/**
	 * FilterCache data that is linked by coordinate is added to parallelData.
	 * 
	 * @param parallelData the parallelData to add filterCache data
	 */
	public void addFilteredData(final ParallelData<T> parallelData) {
		final int conditions = parallelData.getConditions();
		// coorindate of parallelData - identifies linked data in filterCache
		final Coordinate coordinate = parallelData.getCoordinate();

		for (int conditionIndex = 0; conditionIndex < conditions; ++conditionIndex) {
			// replicates for condition
			int replicates = parallelData.getReplicates(conditionIndex);

			// add data from 
			// filterCache(conditionIndex, replicateIndex) 
			// to 
			// ParallelData(conditionIndex, replicateIndex)
			for (int replicateIndex = 0; replicateIndex < replicates; replicateIndex++) {
				filterCaches
					.get(conditionIndex).get(replicateIndex) // data from filterFache 
					.addData(parallelData.getData(conditionIndex, replicateIndex), coordinate); // data from parallelData
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
