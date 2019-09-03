package lib.data.storage;

import lib.data.storage.container.SharedStorage;

/**
 * TODO
 */
public abstract class AbstractStorage implements Storage {
	
	private final SharedStorage sharedStorage;
	
	public AbstractStorage(final SharedStorage sharedStorage) {
		this.sharedStorage = sharedStorage;
	}

	@Override
	public SharedStorage getSharedStorage() {
		return sharedStorage;
	}
	
}
