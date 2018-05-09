package lib.data.cache.record;

import lib.data.AbstractData;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.cache.DataCache;

public interface RecordDataCache<X extends AbstractData> extends DataCache<X> {

	void addRecord(final SAMRecordWrapper recordWrapper);
	
}
