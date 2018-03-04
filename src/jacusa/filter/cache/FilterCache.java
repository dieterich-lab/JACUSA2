package jacusa.filter.cache;

import lib.data.AbstractData;
import lib.data.cache.DataCache;

public interface FilterCache<T extends AbstractData> extends DataCache<T> {

	char getC();

}
