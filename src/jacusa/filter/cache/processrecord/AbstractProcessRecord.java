package jacusa.filter.cache.processrecord;

import lib.data.cache.region.RegionDataCache;

/**
 * This class implements the ProcessRecord interface. 
 */
public abstract class AbstractProcessRecord implements ProcessRecord {

	// TODO add comments.
	private final int distance;
	// ensures that each position is only counted once
	private final RegionDataCache regionCache;
	
	public AbstractProcessRecord(final int distance, 
			final RegionDataCache regionCache) {

		this.distance = distance;
		this.regionCache = regionCache;
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
	public RegionDataCache getRegionCache() {
		return regionCache;
	}
	
}
