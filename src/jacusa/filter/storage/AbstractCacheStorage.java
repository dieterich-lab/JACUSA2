package jacusa.filter.storage;

import lib.data.AbstractData;
import lib.data.builder.SAMRecordWrapper;
import lib.data.cache.Cache;

public abstract class AbstractCacheStorage<T extends AbstractData> 
extends AbstractStorage<T> {

	private Cache<T> cache;

	public AbstractCacheStorage(final char c, Cache<T> cache) {
		super(c);
		this.cache = cache;
	}
	
	protected void addRegion(int windowPosition, int length, int readPosition, final SAMRecordWrapper recordWrapper) {
		// TODO window stuff
		cache.addRecordWrapperRegion(readPosition, length, recordWrapper);		
	}

	public Cache<T> getCache(){
		return cache;
	}

	@Override
	public void clear() {
		cache.clear();
	}

}