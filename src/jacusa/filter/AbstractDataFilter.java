package jacusa.filter;

import java.util.List;

import jacusa.filter.cache.FilterCache;
import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.ParallelData;
import lib.util.coordinate.Coordinate;

public abstract class AbstractDataFilter<T extends AbstractData> 
extends AbstractFilter<T> {

	// first list stores condition and each nested list the respective replicates
	private final List<List<FilterCache<T>>> conditionFilterCaches;
	
	protected AbstractDataFilter(final char c, 
			final int overhang, 
			final AbstractParameter<T, ?> parameter,
			final List<List<FilterCache<T>>> conditionFilterCaches) {
		
		super(c, overhang);
		this.conditionFilterCaches = conditionFilterCaches;
	}

	protected void addFilteredData(final ParallelData<T> parallelData) {
		final int conditions = parallelData.getConditions();
		final Coordinate coordinate = parallelData.getCoordinate();
		
		for (int conditionIndex = 0; conditionIndex < conditions; ++conditionIndex) {
			// replicates for condition
			int replicates = parallelData.getReplicates(conditionIndex);

			// collect data from each replicate
			for (int replicateIndex = 0; replicateIndex < replicates; replicateIndex++) {
				getFilterCache(conditionIndex, replicateIndex).addData(parallelData.getData(conditionIndex, replicateIndex), coordinate);
			}
		}
	}

	public void clear() {
		for (List<FilterCache<T>> replicateFilterCaches : conditionFilterCaches) {
			for (FilterCache<T> replicateFilter : replicateFilterCaches) {
				replicateFilter.clear();
			}
		}
	}

	public FilterCache<T> getFilterCache(final int conditionIndex, final int replicateIndex) {
		return conditionFilterCaches.get(conditionIndex).get(replicateIndex);
	}

}
