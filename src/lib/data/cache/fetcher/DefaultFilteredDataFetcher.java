package lib.data.cache.fetcher;

import lib.data.DataType;
import lib.data.DataTypeContainer;
import lib.data.filter.FilteredData;
import lib.util.Data;

public class DefaultFilteredDataFetcher<F extends FilteredData<F, T>, T extends Data<T>>
implements FilteredDataFetcher<F, T> {

	private final DataTypeFetcher<F> fetcher;
	
	public DefaultFilteredDataFetcher(final DataType<F> dataType) {
		fetcher = new DataTypeFetcher<>(dataType);
	}

	@Override
	public F fetch(DataTypeContainer container) {
		return fetcher.fetch(container);
	}

	@Override
	public DataType<F> getDataType() {
		return fetcher.getDataType();
	}
	
}
