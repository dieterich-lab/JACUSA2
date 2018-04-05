package jacusa.filter;

import jacusa.filter.cache.FilterCache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lib.data.AbstractData;
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

	// map of filters - contains both: AbstractFilter and AbstractDataFilter 
	private final Map<Character, AbstractFilter<T>> filters;
	// map of filters that REQUIRE filterCache data -  only AbstractDataFilter
	private final Map<Character, AbstractDataFilter<T>> dataFilters;

	// max overhang that is required by some filter
	private int overhang;

	public FilterContainer(final FilterConfig<T> filterConfig,
			final CoordinateController coordinateController) {

		this.filterConfig 			= filterConfig;
		this.coordinateController 	= coordinateController;

		overhang 					= 0;

		final int initialCapacity 	= 3;
		filters						= new HashMap<Character, AbstractFilter<T>>(initialCapacity);
		dataFilters					= new HashMap<Character, AbstractDataFilter<T>>(initialCapacity);
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
	 * Helper method. Clears all dataFilters. Usually used after window switch.
	 */
	public void clear() {
		for (final AbstractDataFilter<T> dataFilter : dataFilters.values()) {
			dataFilter.clear();
		}
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
	 * Adds a filter that REQUIRES filterCache data. Implicitly, 
	 * the filter is added to the map of all filters. 
	 * 
	 * @param filter the filter to be added
	 */
	public void addDataFilter(final AbstractDataFilter<T> dataFilter) {
		filters.put(dataFilter.getC(), dataFilter);
		dataFilters.put(dataFilter.getC(), dataFilter);
	}
	
	/**
	 * Processes a recordWrapper and adds it to the 
	 * 
	 * @param c
	 * @param conditionIndex
	 * @param replicateIndex
	 * @param recordWrapper
	 */
	/* FIXME is this needed?
	public void addRecordWrapper(final char c, 
			final int conditionIndex, final int replicateIndex, final SAMRecordWrapper recordWrapper) {
		
		final AbstractDataFilter<T> dataFilter = dataFilters.get(c);
		dataFilter.getFilterCache(conditionIndex, replicateIndex).addRecordWrapper(recordWrapper);
	}
	*/

	public List<FilterCache<?>> getFilterCaches(final int conditionIndex, final int replicateIndex) {
		final List<FilterCache<?>> filterCaches = new ArrayList<FilterCache<?>>();

		for (final AbstractDataFilter<T> dataFilter : dataFilters.values()) {
			filterCaches.add(dataFilter.getFilterCache(conditionIndex, replicateIndex));
		}

		return filterCaches;
	}

	/**
	 * Returns instances of the filters that are active.
	 * 
	 * @return list of active filters
	 */
	public List<AbstractFilter<T>> getFilters() {
		return new ArrayList<AbstractFilter<T>>(filters.values());
	}

}
