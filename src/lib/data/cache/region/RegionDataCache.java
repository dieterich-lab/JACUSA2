package lib.data.cache.region;

import lib.data.adder.DataContainerPopulator;
import lib.data.builder.recordwrapper.SAMRecordWrapper;

public interface RegionDataCache 
extends DataContainerPopulator {

	void addRegion(int referencePosition, int readPosition, int length, SAMRecordWrapper recordWrapper);
		
}
