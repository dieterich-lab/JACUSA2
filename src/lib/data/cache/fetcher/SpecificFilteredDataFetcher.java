package lib.data.cache.fetcher;

import lib.data.DataTypeContainer;
import lib.data.filter.FilteredData;
import lib.util.Data;

public class SpecificFilteredDataFetcher<F extends FilteredData<F, T>, T extends Data<T>>
implements Fetcher<T> {

	private final char c;
	private final FilteredDataFetcher<F, T> filteredDataFetcher;
	
	public SpecificFilteredDataFetcher(final char c, final FilteredDataFetcher<F, T> filteredDataFetcher) {
		this.c = c;
		this.filteredDataFetcher = filteredDataFetcher;
	}

	@Override
	public T fetch(DataTypeContainer container) {
		return filteredDataFetcher.fetch(container).get(c);
	}
	
}
