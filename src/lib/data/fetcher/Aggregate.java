package lib.data.fetcher;

import java.util.List;

import lib.data.Data;
import lib.data.DataContainer;
import lib.data.DataType;

public class Aggregate<T extends Data<T>> implements Fetcher<T> {
	
	private final Class<T> enclosingClass;
	private final List<DataType<T>> dataTypes;
	
	public Aggregate(final Class<T> enclosingClass, final List<DataType<T>> dataTypes) {
		this.enclosingClass = enclosingClass;
		this.dataTypes 		= dataTypes;
	}
	
	@Override
	public T fetch(final DataContainer dataContainer) throws InstantiationException, IllegalAccessException {
		final T data = enclosingClass.newInstance();
		for (final DataType<T> dataType : dataTypes) {
			data.merge(dataContainer.get(dataType));
		}
		return data;
	}
	
}
