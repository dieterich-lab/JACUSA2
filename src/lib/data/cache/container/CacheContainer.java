package lib.data.cache.container;

import java.util.List;

import lib.data.DataTypeContainer;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.cache.record.RecordWrapperProcessor;
import lib.util.coordinate.Coordinate;

public interface CacheContainer {

	void preProcess();
	void process(final SAMRecordWrapper recordWrapper);
	void postProcess();
	
	int getNext(final int windowPosition);
	ReferenceProvider getReferenceProvider();
	
	void populateContainer(DataTypeContainer container, final Coordinate coordinate);
	
	List<RecordWrapperProcessor> getCaches();
	void clear();
	
}
