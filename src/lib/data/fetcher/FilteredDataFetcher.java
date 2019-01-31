package lib.data.fetcher;

import lib.data.Data;
import lib.data.filter.FilteredDataContainer;

public interface FilteredDataFetcher<F extends FilteredDataContainer<F, T>, T extends Data<T>> 
extends DefaultFetcher<F> {

}
