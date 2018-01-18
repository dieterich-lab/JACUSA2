package jacusa.filter;

import java.util.List;

import jacusa.filter.cache.FilterCache;
import jacusa.filter.factory.AbstractDataFilterFactory;
import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.ParallelData;
import lib.data.has.hasLibraryType.LIBRARY_TYPE;
import lib.util.coordinate.Coordinate;

public abstract class AbstractDataFilter<T extends AbstractData, F extends AbstractData> 
extends AbstractFilter<T> {

	private final AbstractDataFilterFactory<T, F> filterFactory;
	private final List<List<FilterCache<F>>> conditionFilterCaches;
	
	protected AbstractDataFilter(final char c, 
			final int overhang, 
			final AbstractParameter<T, ?> parameter,
			final AbstractDataFilterFactory<T, F> dataFilterFactory,
			final List<List<FilterCache<F>>> conditionFilterCaches) {
		
		super(c, overhang);
		this.filterFactory = dataFilterFactory;
		this.conditionFilterCaches = conditionFilterCaches;
	}
	
	public AbstractDataFilterFactory<T, F> getFilterFactory() {
		return filterFactory;
	}

	protected ParallelData<F> getFilteredParallelData(final ParallelData<T> parallelData) {
		final Coordinate coordinate = parallelData.getCoordinate();
		final F[][] filteredDataArray = getFilteredData(parallelData);

		final ParallelData<F> filteredParallelData = 
				new ParallelData<F>(getFilterFactory(), coordinate, filteredDataArray);

		return filteredParallelData;
	}
		
	protected F getFilteredData(final Coordinate coordinate, final LIBRARY_TYPE libraryFype, 
			final int condititonIndex, final int replicateIndex) {
		
		final F filteredData = getFilterFactory().createData(libraryFype, coordinate);
		getFilterCache(condititonIndex, replicateIndex).addData(filteredData, coordinate);
		return filteredData;
	}
	
	protected F[][] getFilteredData(final ParallelData<T> parallelData) {
		final int conditions = parallelData.getConditions();
		final Coordinate coordinate = parallelData.getCoordinate();

		// create container [condition][replicates]
		final F[][] filteredData = getFilterFactory().createContainerData(conditions);

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
		for (List<FilterCache<F>> replicateFilterCaches : conditionFilterCaches) {
			for (FilterCache<F> replicateFilter : replicateFilterCaches) {
				replicateFilter.clear();
			}
		}
	}

	public FilterCache<F> getFilterCache(final int conditionIndex, final int replicateIndex) {
		return conditionFilterCaches.get(conditionIndex).get(replicateIndex);
	}
	
}
