package lib.data.cache.container;

import lib.data.builder.recordwrapper.SAMRecordWrapper;

public interface NextPositionCache {

	public abstract int getNext(int windowPosition);
	public abstract void addRecordWrapper(SAMRecordWrapper recordWrapper);
	public abstract void clear();

}