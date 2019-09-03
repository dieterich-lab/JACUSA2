package jacusa.filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jacusa.filter.factory.FilterFactory;
import lib.cli.parameter.ConditionParameter;
import lib.data.storage.Cache;
import lib.data.storage.container.SharedStorage;
import lib.util.Util;

/**
 * This class holds the instances of filters.
 */
public class FilterContainer {

	// reference to the filterConfig that created this FilterContainer
	private final FilterConfig filterConfig;

	// map of filters - contains both: AbstractFilter and AbstractDataFilter 
	private final Map<Character, Filter> id2filter;

	// max overhang over all filters
	// overhang is defined as the number of nucleotides that are required by
	// a filter down- and or upstream of the current coordinates
	private int overhang;

	public FilterContainer(final FilterConfig filterConfig) {
		this.filterConfig 	= filterConfig;
		overhang 			= 0;
		id2filter			= new HashMap<>(
				Util.noRehashCapacity(filterConfig.getFilterFactories().size()));
	}

	/**
	 * Returns the maximum of all used filters. 
	 * 
	 * @return maximum overhang over all filters
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
	public void addFilter(final Filter filter) {
		id2filter.put(filter.getID(), filter);
	}

	/**
	 * Returns instances of filters that are active.
	 * 
	 * @return list of active filters
	 */
	public List<Filter> getFilters() {
		return Collections.unmodifiableList(new ArrayList<>(id2filter.values()));
	}
	
	/**
	 * Creates Cache for all selected filters.
	 * @param conditionParameter	conditionParameter for one condition
	 * @param sharedStorage			cache that is shared between conditions for ONE thread
	 * @return
	 */
	public Cache createFilterCache(
			final ConditionParameter conditionParameter, 
			final SharedStorage sharedStorage) {
		
		final Cache filterCache = new Cache();
		for (final FilterFactory filterFactory : filterConfig.getFilterFactories()) {
			final Cache tmpCache = filterFactory.createFilterCache(conditionParameter, sharedStorage);
			// Some filterFactories don't have caches
			// therefore != null test needed
			if (tmpCache != null) {
				filterCache.addCache(tmpCache);
			}
		}
		return filterCache;
	}
	
}
