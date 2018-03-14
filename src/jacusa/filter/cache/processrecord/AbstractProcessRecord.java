package jacusa.filter.cache.processrecord;

import lib.data.cache.AbstractUniqueDataCache;

/**
 * This class implements the ProcessRecord interface. 
 */
public abstract class AbstractProcessRecord implements ProcessRecord {

	// TODO add comments.
	private final int distance;
	// ensures that each position is only counted once
	private final AbstractUniqueDataCache<?> uniqueCache;
	
	public AbstractProcessRecord(final int distance, 
			final AbstractUniqueDataCache<?> uniqueCache) {

		this.distance = distance;
		this.uniqueCache = uniqueCache;
	}

	/**
	 * TODO add comments.
	 * 
	 * @return
	 */
	public int getDistance() {
		return distance;
	}
	
	/**
	 * TODO add comments.
	 * 
	 * @return
	 */
	public AbstractUniqueDataCache<?> getUniqueCache() {
		return uniqueCache;
	}
	
}
