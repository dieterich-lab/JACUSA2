package lib.data.cache.fetcher;

import lib.data.filter.FilteredData;
import lib.util.Data;

public interface FilteredDataFetcher<F extends FilteredData<F, T>, T extends Data<T>> 
extends DefaultFetcher<F> {

}
