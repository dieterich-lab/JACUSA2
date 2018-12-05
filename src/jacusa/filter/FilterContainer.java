package jacusa.filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lib.cli.parameter.ConditionParameter;
import lib.data.cache.container.SharedCache;
import lib.data.cache.record.RecordWrapperProcessor;

/**
 * This class holds the instance of filters.
 *
 * @param 
 */
public class FilterContainer {

	// reference to the filterConfig that created this FilterContainer
	private final FilterConfig filterConfig;

	// map of filters - contains both: AbstractFilter and AbstractDataFilter 
	private final Map<Character, AbstractFilter> filters;

	// max overhang that is required by some filter
	private int overhang;

	public FilterContainer(final FilterConfig filterConfig) {
		this.filterConfig 	= filterConfig;
		overhang 			= 0;
		filters				= new HashMap<Character, AbstractFilter>(filterConfig.getFilterFactories().size());
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
	public FilterConfig getFilterConfig() {
		return filterConfig;
	}

	/**
	 * Adds a filter. AbstractDataFilter should be added by <code>addDataFilter()</code>. 
	 * 
	 * @param filter the filter to be added
	 */
	public void addFilter(final AbstractFilter filter) {
		filters.put(filter.getC(), filter);
	}

	/**
	 * Returns instances of filters that are active.
	 * 
	 * @return list of active filters
	 */
	public List<AbstractFilter> getFilters() {
		return Collections.unmodifiableList(new ArrayList<>(filters.values()));
	}
	
	public List<RecordWrapperProcessor> createFilterCaches(final ConditionParameter conditionParameter, final SharedCache sharedCache) {
		return Collections.unmodifiableList(
			filterConfig.getFilterFactories().stream()
				.map(filterFactory -> filterFactory.createFilterCache(conditionParameter, sharedCache))
				.filter(filterCache -> filterCache != null)
				.collect(Collectors.toList()) );
	}
	
}
