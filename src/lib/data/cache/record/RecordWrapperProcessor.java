package lib.data.cache.record;

import lib.data.adder.DataContainerPopulator;
import lib.data.builder.recordwrapper.SAMRecordWrapper;

public interface RecordWrapperProcessor 
extends DataContainerPopulator {

	void preProcess();
	void process(SAMRecordWrapper recordWrapper);
	void postProcess();
	
}
