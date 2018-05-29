package lib.data.cache.region;

import lib.data.AbstractData;
import lib.data.adder.DataAdder;
import lib.data.builder.recordwrapper.SAMRecordWrapper;

public interface RegionDataCache<T extends AbstractData> 
extends DataAdder<T> {

	void addRegion(int referencePosition, int readPosition, int length, 
			SAMRecordWrapper recordWrapper);
		
}
