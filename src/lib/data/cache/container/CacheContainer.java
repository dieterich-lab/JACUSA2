package lib.data.cache.container;

import java.util.List;

import lib.data.AbstractData;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.cache.Cache;
import lib.util.coordinate.Coordinate;

public interface CacheContainer<T extends AbstractData> {

	void addRecordWrapper(final SAMRecordWrapper recordWrapper);
	
	int getNext(final int windowPosition);

	void addData(T data, final Coordinate coordinate);
	
	List<Cache<T>> getCaches();
	void clear();
	
}
