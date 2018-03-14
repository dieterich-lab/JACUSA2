package jacusa.filter.cache;

import lib.data.AbstractData;
import lib.data.cache.DataCache;

/**
 * Abstract class defines a filter cache. 
 * It adds unique char id to DataCache interface. 
 *   
 * @param <F>
 */
public interface FilterCache<T extends AbstractData> extends DataCache<T> {

	/**
	 * Returns the unique char id of the filter this Cache is used for.
	 * 
	 * @return unique char id of filter
	 */
	char getC();

}
