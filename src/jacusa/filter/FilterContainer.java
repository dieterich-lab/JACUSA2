package jacusa.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jacusa.filter.factory.AbstractDataFilterFactory;
import lib.cli.parameter.AbstractConditionParameter;
import lib.data.AbstractData;
import lib.data.cache.record.RecordWrapperDataCache;
import lib.util.coordinate.CoordinateController;

/**
 * This class holds the instance of filters.
 *
 * @param <T>
 */
public class FilterContainer<T extends AbstractData> {

	// reference to the filterConfig that created this FilterContainer
	private final FilterConfig<T> filterConfig;
	// reference to a specific window
	private final CoordinateController coordinateController;

	// map of filterFactories - contains ONLY 
	private final Map<Character, AbstractDataFilterFactory<T>> dataFilterFactories;
	
	// map of filters - contains both: AbstractFilter and AbstractDataFilter 
	private final Map<Character, AbstractFilter<T>> filters;

	// max overhang that is required by some filter
	private int overhang;

	public FilterContainer(final FilterConfig<T> filterConfig,
			final CoordinateController coordinateController) {

		this.filterConfig 			= filterConfig;
		this.coordinateController 	= coordinateController;

		overhang 					= 0;

		final int initialCapacity 	= 3;
		dataFilterFactories			= new HashMap<Character, AbstractDataFilterFactory<T>>(initialCapacity);
		filters						= new HashMap<Character, AbstractFilter<T>>(initialCapacity);
	}

	/**
	 * Returns the maximum of all used filters. 
	 * 
	 * @return maximum overhang of filters
	 */
	public int getOverhang() {
		return overhang;
	}

	/**
	 * Returns the filterConfig object that created this FilterContainer.
	 * 
	 * @return filterConfig that created this FilterContainer.
	 */
	public FilterConfig<T> getFilterConfig() {
		return filterConfig;
	}
	
	/**
	 * Returns that coordinateController that defines a window within the BAM files.
	 * 
	 * @return coordinateController linked to this FilterContainer
	 */
	public CoordinateController getCoordinateController() {
		return coordinateController;
	}

	/**
	 * Adds a filter. AbstractDataFilter should be added by <code>addDataFilter()</code>. 
	 * 
	 * @param filter the filter to be added
	 */
	public void addFilter(final AbstractFilter<T> filter) {
		filters.put(filter.getC(), filter);
	}
	
	/**
	 * Adds a filter. AbstractDataFilter should be added by <code>addDataFilter()</code>. 
	 * 
	 * @param dataFilterFactory the filter to be added
	 */
	public void addDataFilterFactory(final AbstractDataFilterFactory<T> dataFilterFactory) {
		dataFilterFactories.put(dataFilterFactory.getC(), dataFilterFactory);
	}

	/**
	 * Returns instances of the filters that are active.
	 * 
	 * @return list of active filters
	 */
	public List<AbstractFilter<T>> getFilters() {
		return new ArrayList<AbstractFilter<T>>(filters.values());
	}

	public List<RecordWrapperDataCache<T>> createFilterCaches(final AbstractConditionParameter<T> conditionParameter, final CoordinateController coordinateController) {
		final List<RecordWrapperDataCache<T>> filterCaches = 
				new ArrayList<RecordWrapperDataCache<T>>(dataFilterFactories.size());

		for (final AbstractDataFilterFactory<T> dataFilterFactory : dataFilterFactories.values()) {
			filterCaches.add(dataFilterFactory.createFilterCache(conditionParameter, coordinateController));
		}
		
		return filterCaches;
	}
	
}
