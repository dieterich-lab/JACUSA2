package lib.data.cache.record;

import lib.data.adder.DataContainerAdder;
import lib.data.builder.recordwrapper.SAMRecordWrapper;

public interface RecordWrapperDataCache 
extends DataContainerAdder {

	void processRecordWrapper(SAMRecordWrapper recordWrapper);
	
}
