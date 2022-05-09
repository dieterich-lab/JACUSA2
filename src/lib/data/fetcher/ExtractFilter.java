package lib.data.fetcher;

import lib.data.Data;
import lib.data.DataContainer;
import lib.data.DataType;
import lib.data.filter.AbstractFilteredData;

public class ExtractFilter<V extends AbstractFilteredData<V, T>, T extends Data<T>> implements Fetcher<T> {
	
	private final char id;
	private final DataType<V> dataType;
	
	public ExtractFilter(final char id, final DataType<V> dataType) {
		this.id = id;
		this.dataType = dataType;
	}
	
	@Override
	public T fetch(final DataContainer dataContainer) {
		return dataContainer.get(dataType).get(id);
	}
	
}
