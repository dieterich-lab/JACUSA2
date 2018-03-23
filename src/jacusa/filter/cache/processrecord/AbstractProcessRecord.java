package jacusa.filter.cache.processrecord;

import lib.data.cache.region.UniqueRegionDataCache;

/**
 * This class implements the ProcessRecord interface. 
 */
public abstract class AbstractProcessRecord implements ProcessRecord {

	// TODO add comments.
	private final int distance;
	// ensures that each position is only counted once
	private final UniqueRegionDataCache<?> uniqueCache;
	
	public AbstractProcessRecord(final int distance, 
			final UniqueRegionDataCache<?> uniqueCache) {

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
	public UniqueRegionDataCache<?> getUniqueCache() {
		return uniqueCache;
	}
	
}
