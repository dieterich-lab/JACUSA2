package jacusa.filter;

import jacusa.filter.factory.AbstractFilterFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lib.data.AbstractData;
import lib.data.builder.ConditionContainer;
import lib.util.coordinate.CoordinateController;

/**
 * This class holds the configuration of chosen filters by storing 
 * the factories of the respective filters.
 * 
 * @param <T>
 */
public class FilterConfig<T extends AbstractData> implements Cloneable {

	// Map holds chosen filter factories, indexed by unique char id
	private final Map<Character, AbstractFilterFactory<T>> c2factory;
	
	public FilterConfig() {
		c2factory = new HashMap<Character, AbstractFilterFactory<T>>(6);
	}

	/**
	 * Adds a filterFactory to the list of active filters. Can be only added once
	 * otherwise an Exception is thrown.
	 * 
	 * @param filterFactory filterFactory to be added
	 * @throws Exception if filter has been already added
	 */
	public void addFactory(final AbstractFilterFactory<T> filterFactory) throws Exception {
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
	public FilterContainer<T> createFilterInstances(final CoordinateController coordinateController) {
		return new FilterContainer<T>(this, coordinateController);
	}
	
	/**
	 * TODO add comments
	 * FIXME add this to addFactory or createFilterInstance
	 * 
	 * @param coordinateController
	 * @param conditionContainer
	 */
	public void registerFilters(final CoordinateController coordinateController, final ConditionContainer<T> conditionContainer) {
		for (final AbstractFilterFactory<T> filterFactory : c2factory.values()) {
			filterFactory.registerFilter(coordinateController, conditionContainer);
		}
	}

	/**
	 * Indicates if any filter has been configured.
	 * 
	 * @return true if any filter has been added
	 */
	public boolean hasFiters() {
		return c2factory.size() > 0;
	}

	/**
	 * Returns a list of the chosen filters.
	 * 
	 * @return a list of FilterFactories
	 */
	public List<AbstractFilterFactory<T>> getFilterFactories() {
		return new ArrayList<AbstractFilterFactory<T>>(c2factory.values());
	}
	
}
