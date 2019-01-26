package lib.data.adder;

import lib.data.cache.container.SharedCache;

public abstract class AbstractDataContainerPopulator
implements DataContainerPopulator {
	
	private final SharedCache sharedCache;
	
	public AbstractDataContainerPopulator(final SharedCache sharedCache) {
		this.sharedCache = sharedCache;
	}

	@Override
	public SharedCache getShareCache() {
		return sharedCache;
	}
}
