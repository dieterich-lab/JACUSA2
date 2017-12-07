package lib.data.cache.container;

import lib.data.builder.recordwrapper.SAMRecordWrapper;

public interface GeneralCache {

	int getNext(int windowPosition);
	byte getReference(int windowPosition);
	
	void addRecordWrapper(SAMRecordWrapper recordWrapper);
	void clear();

}