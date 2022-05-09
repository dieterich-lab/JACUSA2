package lib.data.storage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lib.data.DataType;
import lib.data.IntegerData;
import lib.data.storage.container.SharedStorage;
import lib.data.storage.integer.ArrayIntegerStorage;
import lib.data.storage.integer.MapIntegerStorage;
import lib.data.storage.processor.CoverageRecordProcessor;
import lib.data.storage.processor.DeletionRecordProcessor;
import lib.data.storage.processor.GeneralRecordProcessor;
import lib.data.storage.processor.InsertionRecordProcessor;
import lib.util.coordinate.CoordinateTranslator;

/**
 * TODO
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
	
	static public Cache createInsertionCache(final SharedStorage sharedStorage, final DataType<IntegerData> dataType) {
		final Cache cache = new Cache();

		final Storage insStorage = new MapIntegerStorage(sharedStorage, dataType);
		cache.addStorage(insStorage);

		final CoordinateTranslator translator = sharedStorage.getCoordinateController().getCoordinateTranslator();

		cache.addRecordProcessor(new InsertionRecordProcessor(translator, insStorage));

		return cache;
	}
	
	static public Cache createDeletionCache(final SharedStorage sharedStorage, final DataType<IntegerData> dataType) {
		final Cache cache = new Cache();

		final Storage storage = new MapIntegerStorage(sharedStorage, dataType);
		cache.addStorage(storage);

		final CoordinateTranslator translator = sharedStorage.getCoordinateController().getCoordinateTranslator();

		cache.addRecordProcessor(new DeletionRecordProcessor(translator, storage));

		return cache;
	}
	
	static public Cache createReadCoverageCache(final SharedStorage sharedStorage, final DataType<IntegerData> dataType) {
		final Cache cache = new Cache();

		final Storage storage = new ArrayIntegerStorage(sharedStorage, dataType);
		cache.addStorage(storage);

		final CoordinateTranslator translator = sharedStorage.getCoordinateController().getCoordinateTranslator();

		cache.addRecordProcessor(new CoverageRecordProcessor(translator, storage));
		return cache;
	}
	
}
