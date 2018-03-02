package jacusa.filter;

import java.util.List;

import jacusa.filter.cache.FilterCache;
import jacusa.filter.factory.AbstractDataFilterFactory;
import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.ParallelData;
import lib.data.has.hasLibraryType.LIBRARY_TYPE;
import lib.util.coordinate.Coordinate;

public abstract class AbstractDataFilter<T extends AbstractData> 
extends AbstractFilter<T> {

	private final List<List<FilterCache<T>>> conditionFilterCaches;
	
	protected AbstractDataFilter(final char c, 
			final int overhang, 
			final AbstractParameter<T, ?> parameter,
			final List<List<FilterCache<T>>> conditionFilterCaches) {
		
		super(c, overhang);
		this.conditionFilterCaches = conditionFilterCaches;
	}

	protected ParallelData<T> getFilteredParallelData(final ParallelData<T> parallelData) {
		return new ParallelData<T>(parallelData.getDataGenerator(), getFilteredData(parallelData));
	}
		
	protected T getFilteredData(final Coordinate coordinate, final LIBRARY_TYPE libraryFype, 
			final int condititonIndex, final int replicateIndex) {
		
		final T filteredData = getFilterFactory().createData(libraryFype, coordinate);
		getFilterCache(condititonIndex, replicateIndex).addData(filteredData, coordinate);
		return filteredData;
	}
	
	protected T[][] getFilteredData(final ParallelData<T> parallelData) {
		final int conditions = parallelData.getConditions();
		final Coordinate coordinate = parallelData.getCombinedPooledData().getCoordinate();

		// create container [condition][replicates]
		final T[][] filteredData = getFilterFactory().createContainerData(conditions);

		for (int conditionIndex = 0; conditionIndex < conditions; ++conditionIndex) {
			// replicates for condition
			int replicates = parallelData.getReplicates(conditionIndex);
			
			// container for replicates of a condition
			filteredData[conditionIndex] = getFilterFactory().createReplicateData(replicates);

			// collect data from each replicate
			for (int replicateIndex = 0; replicateIndex < replicates; replicateIndex++) {
				final LIBRARY_TYPE libraryType = parallelData.getData(conditionIndex, replicateIndex).getLibraryType(); 
				filteredData[conditionIndex][replicateIndex] = getFilteredData(coordinate, libraryType, conditionIndex, replicateIndex);
			}
		}
		
		return filteredData;
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
