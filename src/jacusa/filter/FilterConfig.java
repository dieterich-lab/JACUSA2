package jacusa.filter;

import jacusa.cli.parameters.ConditionParameters;
import jacusa.data.AbstractData;

import jacusa.filter.factory.AbstractFilterFactory;
import jacusa.util.Coordinate.STRAND;
import jacusa.util.WindowCoordinate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	/**
	 * Create CountFilterCache for each available filter.
	 * Info: some filters might not need the cache
	 * 
	 * @return
	 */
	public FilterContainer<T> createFilterContainer(final WindowCoordinate windowCoordinates, 
			final STRAND strand, final ConditionParameters<T> condition) {
		
		FilterContainer<T> filterContainer = new FilterContainer<T>(
				this, strand, windowCoordinates, condition);
		
		for (final AbstractFilterFactory<T> filterFactory : c2factory.values()) {
			filterFactory.registerFilter(filterContainer);
		}
		
		return filterContainer;
	}

	public boolean hasFiters() {
		return c2factory.size() > 0;
	}

	public boolean hasFilter(final char c) {
		return c2factory.containsKey(c);
	}
	
	public List<AbstractFilterFactory<T>> getFactories() {
		return new ArrayList<AbstractFilterFactory<T>>(c2factory.values());
	}
	
}