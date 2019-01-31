package lib.data.storage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lib.data.storage.processor.RecordExtendedPrePostProcessor;
import lib.data.stroage.Storage;

public class Cache {

	private final List<RecordExtendedPrePostProcessor> processors;
	private final List<Storage> storages;
	
	public Cache() {
		processors 	= new ArrayList<RecordExtendedPrePostProcessor>();
		storages 	= new ArrayList<>();
	}

	public void addCache(final Cache cache) {
		processors.addAll(cache.processors);
		storages.addAll(cache.storages);
	}
	
	public void addCaches(final List<Cache> caches) {
		for (final Cache cache : caches) {
			addCache(cache);
		}
	}
	
	public void addStorage(final Storage storage) {
		storages.add(storage);
	}
	
	public void addRecordProcessor(final RecordExtendedPrePostProcessor processor) {
		processors.add(processor);
	}
	
	public void addStorages(final List<Storage> storages) {
		this.storages.addAll(storages);
	}
	
	public void addRecordProcessors(final List<RecordExtendedPrePostProcessor> processors) {
		this.processors.addAll(processors);
	}
	
	public List<RecordExtendedPrePostProcessor> getRecordProcessors() {
		return Collections.unmodifiableList(processors);
	}
	
	public List<Storage> getStorages() {
		return Collections.unmodifiableList(storages);
	}
	
}
