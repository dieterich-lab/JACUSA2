package jacusa.filter;

import jacusa.filter.cache.FilterCache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lib.data.AbstractData;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.util.coordinate.CoordinateController;

/**
 * 
 * @author Michael Piechotta
 *
 */
public class FilterContainer<T extends AbstractData> {

	private final FilterConfig<T> filterConfig;
	private final CoordinateController coordinateController;

	private final Map<Character, AbstractFilter<T>> filters;
	private final Map<Character, AbstractDataFilter<T>> dataFilters;

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

	public int getOverhang() {
		return overhang;
	}

	public FilterConfig<T> getFilterConfig() {
		return filterConfig;
	}
	
	public CoordinateController getCoordinateController() {
		return coordinateController;
	}

	public void clear() {
		for (final AbstractDataFilter<T> dataFilter : dataFilters.values()) {
			dataFilter.clear();
		}
	}

	public void addFilter(final AbstractFilter<T> filter) {
		filters.put(filter.getC(), filter);
	}

	public void addDataFilter(final AbstractDataFilter<T> dataFilter) {
		filters.put(dataFilter.getC(), dataFilter);
		dataFilters.put(dataFilter.getC(), dataFilter);
	}
	
	public void addRecordWrapper(final char c, 
			final int conditionIndex, final int replicateIndex, final SAMRecordWrapper recordWrapper) {
		
		final AbstractDataFilter<T> dataFilter = dataFilters.get(c);
		dataFilter.getFilterCache(conditionIndex, replicateIndex).addRecordWrapper(recordWrapper);
	}

	public List<FilterCache<?>> getFilterCaches(final int conditionIndex, final int replicateIndex) {
		final List<FilterCache<?>> filterCaches = new ArrayList<FilterCache<?>>();

		for (final AbstractDataFilter<T> dataFilter : dataFilters.values()) {
			filterCaches.add(dataFilter.getFilterCache(conditionIndex, replicateIndex));
		}

		return filterCaches;
	}

	public List<AbstractFilter<T>> getFilters() {
		return new ArrayList<AbstractFilter<T>>(filters.values());
	}

}
