package lib.data.cache.container;

import java.util.List;

import lib.data.DataTypeContainer;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.cache.record.RecordWrapperDataCache;
import lib.util.coordinate.Coordinate;

public interface CacheContainer {

	void process(final SAMRecordWrapper recordWrapper);
	
	int getNext(final int windowPosition);
	ReferenceProvider getReferenceProvider();
	
	void populateContainer(DataTypeContainer container, final Coordinate coordinate);
	
	List<RecordWrapperDataCache> getCaches();
	void clear();
	
}
