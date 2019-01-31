package lib.data.fetcher;

import lib.data.DataType;
import lib.data.Data;
import lib.data.DataContainer;
import lib.data.filter.FilteredDataContainer;

public class DefaultFilteredDataFetcher<F extends FilteredDataContainer<F, T>, T extends Data<T>>
implements FilteredDataFetcher<F, T> {

	private final DataTypeFetcher<F> fetcher;
	
	public DefaultFilteredDataFetcher(final DataType<F> dataType) {
		fetcher = new DataTypeFetcher<>(dataType);
	}

	@Override
	public F fetch(DataContainer container) {
		return fetcher.fetch(container);
	}

	@Override
	public DataType<F> getDataType() {
		return fetcher.getDataType();
	}
	
}
