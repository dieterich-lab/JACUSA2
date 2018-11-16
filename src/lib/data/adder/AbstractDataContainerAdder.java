package lib.data.adder;

import lib.data.cache.container.ReferenceProvider;
import lib.data.cache.container.SharedCache;
import lib.util.coordinate.CoordinateController;

public abstract class AbstractDataContainerAdder
implements DataContainerPopulator {
	
	private final SharedCache sharedCache;
	
	public AbstractDataContainerAdder(final SharedCache sharedCache) {
		this.sharedCache = sharedCache;
	}
	
	public SharedCache getShareCache() {
		return sharedCache;
	}
	
	public CoordinateController getCoordinateController() {
		return sharedCache.getCoordinateController();
	}
	
	public ReferenceProvider getReferenceProvider() {
		return sharedCache.getReferenceProvider();
	}
}
