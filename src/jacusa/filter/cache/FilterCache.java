package jacusa.filter.cache;

import lib.data.AbstractData;
import lib.data.cache.DataCache;

public interface FilterCache<F extends AbstractData> extends DataCache<F> {
	
	char getC();
	
}
