package lib.data.cache.fetcher;

import lib.data.DataType;
import lib.util.Data;

public interface DefaultFetcher<T extends Data<T>> 
extends Fetcher<T> {

	DataType<T> getDataType();
	
}
