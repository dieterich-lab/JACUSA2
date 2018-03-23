package lib.data.cache.region;

import lib.data.AbstractData;
import lib.data.builder.recordwrapper.SAMRecordWrapper;

public interface UniqueRegionDataCache<X extends AbstractData> extends RegionDataCache<X> {

	void resetVisited(final SAMRecordWrapper recordWrapper);

}
