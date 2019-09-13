package jacusa.filter;

import jacusa.filter.factory.FilterFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lib.util.ConditionContainer;
import lib.util.coordinate.CoordinateController;

/**
 * This class holds the configuration of chosen filters by storing 
 * the factories of the respective filters.
 */
public class FilterConfig implements Cloneable {

	// Map holds chosen filter factories, indexed by unique char id
	private final Map<Character, FilterFactory> id2factory;
	
	public FilterConfig() {
		id2factory = new HashMap<Character, FilterFactory>();
	}

	/**
	 * Adds a filterFactory to the list of active filters. Can be only added once
	 * otherwise an Exception is thrown.
	 * 
	 * @param filterFactory filterFactory to be added
	 * @throws Exception if filter has been already added
	 */
	public void addFactory(final FilterFactory filterFactory) throws Exception {
		final char id = filterFactory.getID();

		if (id2factory.containsKey(id)) {
			throw new Exception("Duplicate value: " + id);
		} else {
			id2factory.put(id, filterFactory);	
		}
	}

	/**
	 * Create FilterContainers with the current filter configuration.
	 * 
	 * @return FiterContainer with current filter configuration
	 */
	public FilterContainer createFilterContainer() {
		return new FilterContainer(this);
	}
	
	/**
	 * Register configured filters by supplying coordinatesController and 
	 * all a conditionContainer
	 * 
	 * @param coordinateController controls and translates coordinates
	 * @param conditionContainer contains condition related data structures
	 */
	public void registerFilters(
			final CoordinateController coordinateController, 
			final ConditionContainer conditionContainer) {

		for (final FilterFactory filterFactory : id2factory.values()) {
			filterFactory.registerFilter(coordinateController, conditionContainer);
		}
	}

	/**
	 * Indicates if any filter has been selected.
	 * 
	 * @return true if at least one filter has been added
	 */
	public boolean hasFiters() {
		return ! id2factory.isEmpty();
	}

	/**
	 * Returns a list of selected filters.
	 * 
	 * @return a list of FilterFactories
	 */
	public List<FilterFactory> getFilterFactories() {
		return new ArrayList<>(id2factory.values());
	}
	
}
