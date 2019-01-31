package lib.data.fetcher;

import lib.data.Data;
import lib.data.DataContainer;
import lib.data.filter.FilteredDataContainer;

public class SpecificFilteredDataFetcher<F extends FilteredDataContainer<F, T>, T extends Data<T>>
implements Fetcher<T> {

	private final char c;
	private final FilteredDataFetcher<F, T> filteredDataFetcher;
	
	public SpecificFilteredDataFetcher(final char c, final FilteredDataFetcher<F, T> filteredDataFetcher) {
		this.c = c;
		this.filteredDataFetcher = filteredDataFetcher;
	}

	@Override
	public T fetch(DataContainer container) {
		return filteredDataFetcher.fetch(container).get(c);
	}
	
}
