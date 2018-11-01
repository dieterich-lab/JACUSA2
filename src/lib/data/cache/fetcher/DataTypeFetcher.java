package lib.data.cache.fetcher;

import java.io.Serializable;

import lib.data.DataType;
import lib.data.DataTypeContainer;
import lib.util.Data;

public class DataTypeFetcher<T extends Data<T>> 
implements DefaultFetcher<T>, Serializable {

	private static final long serialVersionUID = 1L;

	private final DataType<T> dataType;
	
	public DataTypeFetcher(final DataType<T> dataType) {
		this.dataType = dataType;
	}
	
	@Override
	public T fetch(DataTypeContainer container) {
		return container.get(dataType);
	}

	public DataType<T> getDataType() {
		return dataType;
	}
	
}