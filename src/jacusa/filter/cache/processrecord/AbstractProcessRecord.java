package jacusa.filter.cache.processrecord;

import lib.data.cache.UniqueBaseCallDataCache;

public abstract class AbstractProcessRecord implements ProcessRecord {

	private int distance;
	private UniqueBaseCallDataCache<?> uniqueBaseCallCache;
	
	public AbstractProcessRecord(final int distance, final UniqueBaseCallDataCache<?> uniqueBaseCallCache) {
		this.distance = distance;
		this.uniqueBaseCallCache = uniqueBaseCallCache;
	}

	public int getDistance() {
		return distance;
	}
	
	public UniqueBaseCallDataCache<?> getCache() {
		return uniqueBaseCallCache;
	}
	
}
