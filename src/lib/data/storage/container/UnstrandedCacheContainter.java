package lib.data.storage.container;

import java.util.List;

import lib.data.DataContainer;
import lib.data.storage.Cache;
import lib.data.storage.Storage;
import lib.data.storage.processor.ExtendedRecordProcessor;
import lib.record.Record;
import lib.util.coordinate.Coordinate;

public class UnstrandedCacheContainter 
implements CacheContainer {

	private final SharedStorage sharedStorage;
	private final List<ExtendedRecordProcessor> recordProcessors;
	private final List<Storage> storages;
	
	public UnstrandedCacheContainter(
			final SharedStorage sharedStorage, 
			final Cache cache) {

		this.sharedStorage		= sharedStorage;
		this.recordProcessors 	= cache.getRecordProcessors();
		this.storages			= cache.getStorages();
	}
	
	@Override
	public ReferenceProvider getReferenceProvider() {
		return sharedStorage.getReferenceProvider();
	}
	
	@Override
	public int getNextWindowPosition(final int winPos) {
		return sharedStorage.getNext(winPos);
	}
	
	@Override
	public void preProcess() {
		for (final ExtendedRecordProcessor processor : recordProcessors) {
			processor.preProcess();
		}
	}
	
	@Override
	public void process(final Record record) {
		sharedStorage.addrecord(record);

		for (final ExtendedRecordProcessor recordProcessor : recordProcessors) {
			recordProcessor.process(record);
		}
	}
	
	@Override
	public void postProcess() {
		for (final ExtendedRecordProcessor processor : recordProcessors) {
			processor.postProcess();
		}
	}
	
	@Override
	public void populate(DataContainer dataContainer, Coordinate coordinate) {
		final int winPos = sharedStorage
				.getCoordinateController().getCoordinateTranslator()
				.coord2winPos(coordinate);
		for (final Storage cache : storages) {
			cache.populate(dataContainer, winPos, coordinate);
		}
	}
	
	@Override
	public void clearSharedStorage() {
		sharedStorage.clear();
	}

	@Override
	public List<ExtendedRecordProcessor> getRecordProcessors() {
		return recordProcessors;
	}
	
	@Override
	public List<Storage> getStorages() {
		return storages;
	}
	
}
