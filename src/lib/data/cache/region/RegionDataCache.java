package lib.data.cache.region;

import lib.data.adder.DataContainerAdder;
import lib.data.builder.recordwrapper.SAMRecordWrapper;

public interface RegionDataCache 
extends DataContainerAdder {

	void addRegion(int referencePosition, int readPosition, int length, SAMRecordWrapper recordWrapper);
		
}
