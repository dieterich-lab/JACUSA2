package lib.data.cache.region;

import lib.data.AbstractData;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.cache.DataCache;

public interface RegionDataCache<X extends AbstractData> 
extends DataCache<X> {

	void addRegion(final int referencePosition, int readPosition, int length, SAMRecordWrapper recordWrapper);
		
}
