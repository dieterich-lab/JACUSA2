package lib.data.cache.container;

import lib.data.builder.recordwrapper.SAMRecordWrapper;

public interface ReferenceCache {

	void addRecordWrapper(final SAMRecordWrapper recordWrapper);
	byte getReferenceBase(final int windowPosition);
	public void clear();
	
}
