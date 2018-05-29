package lib.data.cache.record;

import lib.data.AbstractData;
import lib.data.adder.DataAdder;
import lib.data.builder.recordwrapper.SAMRecordWrapper;

public interface RecordWrapperDataCache<T extends AbstractData> 
extends DataAdder<T> {

	void addRecordWrapper(SAMRecordWrapper recordWrapper);
	
}
