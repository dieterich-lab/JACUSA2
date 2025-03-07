package lib.data.storage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lib.data.storage.processor.GeneralRecordProcessor;

/**
 * TODO add documentation
 */
public class Cache {

	private final List<GeneralRecordProcessor> processors;
	private final List<Storage> storages;
	
	public Cache() {
		processors 	= new ArrayList<>();
		storages 	= new ArrayList<>();
	}

	public void addCache(final Cache cache) {
		processors.addAll(cache.processors);
		storages.addAll(cache.storages);
	}
	
	public void addStorage(final Storage storage) {
		storages.add(storage);
	}
	
	public void addRecordProcessor(final GeneralRecordProcessor processor) {
		processors.add(processor);
	}
	
	public void addStorages(final List<Storage> storages) {
		this.storages.addAll(storages);
	}
	
	public void addRecordProcessors(final List<GeneralRecordProcessor> processors) {
		this.processors.addAll(processors);
	}
	
	public List<GeneralRecordProcessor> getRecordProcessors() {
		return Collections.unmodifiableList(processors);
	}
	
	public List<Storage> getStorages() {
		return Collections.unmodifiableList(storages);
	}
	
}
