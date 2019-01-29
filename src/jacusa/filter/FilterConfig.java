package jacusa.filter;

import jacusa.filter.factory.AbstractFilterFactory;
import jacusa.filter.factory.FilterFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lib.data.assembler.ConditionContainer;
import lib.util.coordinate.CoordinateController;

/**
 * This class holds the configuration of chosen filters by storing 
 * the factories of the respective filters.
 * 
 * @param 
 */
public class FilterConfig implements Cloneable {

	// Map holds chosen filter factories, indexed by unique char id
	private final Map<Character, AbstractFilterFactory> c2factory;
	
	public FilterConfig() {
		c2factory = new HashMap<Character, AbstractFilterFactory>(6);
	}

	/**
	 * Adds a filterFactory to the list of active filters. Can be only added once
	 * otherwise an Exception is thrown.
	 * 
	 * @param filterFactory filterFactory to be added
	 * @throws Exception if filter has been already added
	 */
	public void addFactory(final AbstractFilterFactory filterFactory) throws Exception {
		final char c = filterFactory.getC();

		if (c2factory.containsKey(c)) {
			throw new Exception("Duplicate value: " + c);
		} else {
			c2factory.put(c, filterFactory);	
		}
	}

	/**
	 * TODO add comments
	 * 
	 * @param coordinateController
	 * @return
	 */
	public FilterContainer createFilterInstances() {
		return new FilterContainer(this);
	}
	
	/**
	 * TODO add comments
	 * 
	 * @param coordinateController
	 * @param conditionContainer
	 */
	public void registerFilters(final CoordinateController coordinateController, final ConditionContainer conditionContainer) {
		for (final FilterFactory filterFactory : c2factory.values()) {
			filterFactory.registerFilter(coordinateController, conditionContainer);
		}
	}

	/**
	 * Indicates if any filter has been configured.
	 * 
	 * @return true if at least one filter has been added
	 */
	public boolean hasFiters() {
		return ! c2factory.isEmpty();
	}

	/**
	 * Returns a list of the chosen filters.
	 * 
	 * @return a list of FilterFactories
	 */
	public List<AbstractFilterFactory> getFilterFactories() {
		return new ArrayList<AbstractFilterFactory>(c2factory.values());
	}
	
}
