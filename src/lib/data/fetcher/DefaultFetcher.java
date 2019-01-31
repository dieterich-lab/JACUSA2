package lib.data.fetcher;

import lib.data.Data;
import lib.data.DataType;

public interface DefaultFetcher<T extends Data<T>> 
extends Fetcher<T> {

	DataType<T> getDataType();
	
}
