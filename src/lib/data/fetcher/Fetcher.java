package lib.data.fetcher;

import lib.data.Data;
import lib.data.DataContainer;

public interface Fetcher<T extends Data<T>> {
	
	public T fetch(final DataContainer dataContainer) throws Exception;
	
}
