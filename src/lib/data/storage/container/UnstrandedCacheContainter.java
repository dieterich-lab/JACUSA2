package lib.data.storage.container;

import java.util.List;

import lib.data.DataContainer;
import lib.data.storage.Cache;
import lib.data.storage.Storage;
import lib.data.storage.processor.GeneralRecordProcessor;
import lib.record.ProcessedRecord;
import lib.util.coordinate.Coordinate;

public class UnstrandedCacheContainter 
implements CacheContainer {

	private final SharedStorage sharedStorage;
	private final List<GeneralRecordProcessor> recordProcessors;
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
		for (final GeneralRecordProcessor processor : recordProcessors) {
			processor.preProcess();
		}
	}
	
	@Override
	public void process(final ProcessedRecord record) {
		sharedStorage.addRecord(record);

		for (final GeneralRecordProcessor recordProcessor : recordProcessors) {
			recordProcessor.process(record);
		}
	}
	
	@Override
	public void postProcess() {
		for (final GeneralRecordProcessor processor : recordProcessors) {
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
	public List<GeneralRecordProcessor> getRecordProcessors() {
		return recordProcessors;
	}
	
	@Override
	public List<Storage> getStorages() {
		return storages;
	}
	
}
