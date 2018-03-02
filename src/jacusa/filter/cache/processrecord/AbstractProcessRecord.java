package jacusa.filter.cache.processrecord;

import lib.data.cache.AbstractUniqueDataCache;

public abstract class AbstractProcessRecord implements ProcessRecord {

	private int distance;
	private AbstractUniqueDataCache<?> uniqueCache;
	
	public AbstractProcessRecord(final int distance, final AbstractUniqueDataCache<?> uniqueCache) {
		this.distance = distance;
		this.uniqueCache = uniqueCache;
	}

	public int getDistance() {
		return distance;
	}
	
	public AbstractUniqueDataCache<?> getCache() {
		return uniqueCache;
	}
	
}
