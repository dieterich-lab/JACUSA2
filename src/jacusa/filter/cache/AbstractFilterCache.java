package jacusa.filter.cache;

import lib.data.AbstractData;

/**
 * Abstract class defines a filter cache. 
 * It adds unique char id to DataCache interface. 
 *   
 * @param <F>
 */
public abstract class AbstractFilterCache<F extends AbstractData> implements FilterCache<F> {

	// unique char id - corresponds to CLI option 
	private final char c;
	
	public AbstractFilterCache(final char c) {
		this.c = c;
	}

	@Override
	public final char getC() {
		return c;
	}

}
