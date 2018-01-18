package jacusa.filter;

import jacusa.filter.factory.AbstractFilterFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lib.data.AbstractData;
import lib.data.builder.ConditionContainer;
import lib.tmp.CoordinateController;

/**
 * 
 * @author Michael Piechotta
 *
 */
public class FilterConfig<T extends AbstractData> implements Cloneable {

	private final Map<Character, AbstractFilterFactory<T>> c2factory;
	
	public FilterConfig() {
		c2factory = new HashMap<Character, AbstractFilterFactory<T>>(6);
	}

	/**
	 * 
	 * @param filterFactory
	 * @throws Exception
	 */
	public void addFactory(final AbstractFilterFactory<T> filterFactory) throws Exception {
		final char c = filterFactory.getC();

		if (c2factory.containsKey(c)) {
			throw new Exception("Duplicate value: " + c);
		} else {
			c2factory.put(c, filterFactory);	
		}
	}

	/*
	/**
	 * Create CountFilterCache for each available filter.
	 * Info: some filters might not need the cache
	 * 
	 * @return
	 */
	public FilterContainer<T> createFilterInstances(final CoordinateController coordinateController, final ConditionContainer<T> conditionContainer) {
		final FilterContainer<T> filterContainer = new FilterContainer<T>(this, coordinateController);
		for (final AbstractFilterFactory<T> filterFactory : c2factory.values()) {
			filterFactory.registerFilter(coordinateController, conditionContainer);
		}
		return filterContainer;
	}

	public boolean hasFiters() {
		return c2factory.size() > 0;
	}

	public boolean hasFilter(final char c) {
		return c2factory.containsKey(c);
	}
	
	public List<AbstractFilterFactory<T>> getFilterFactories() {
		return new ArrayList<AbstractFilterFactory<T>>(c2factory.values());
	}
	
}
