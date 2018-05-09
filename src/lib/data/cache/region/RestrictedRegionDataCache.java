package lib.data.cache.region;

import lib.data.AbstractData;

public interface RestrictedRegionDataCache<X extends AbstractData> 
extends RegionDataCache<X> {

	// TODO add reference
	boolean isValid(int windowPosition, int readPosition, int baseIndex, byte baseQuality);
	void increment(int windowPosition, int readPosition, int baseIndex, byte baseQuality);
		
}
