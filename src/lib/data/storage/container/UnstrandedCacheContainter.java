package lib.data.storage.container;

import java.util.List;

import lib.data.DataContainer;
import lib.data.storage.Cache;
import lib.data.storage.processor.RecordExtendedPrePostProcessor;
import lib.data.stroage.Storage;
import lib.util.coordinate.Coordinate;
import lib.recordextended.SAMRecordExtended;

public class UnstrandedCacheContainter 
implements CacheContainer {

	private final SharedStorage sharedStorage;
	private final List<RecordExtendedPrePostProcessor> recordProcessors;
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
		for (final RecordExtendedPrePostProcessor processor : recordProcessors) {
			processor.preProcess();
		}
	}
	
	@Override
	public void process(final SAMRecordExtended recordExtended) {
		sharedStorage.addRecordExtended(recordExtended);

		for (final RecordExtendedPrePostProcessor recordProcessor : recordProcessors) {
			recordProcessor.process(recordExtended);
		}
	}
	
	@Override
	public void postProcess() {
		for (final RecordExtendedPrePostProcessor processor : recordProcessors) {
			processor.postProcess();
		}
	}
	
	@Override
	public void populate(DataContainer dataContainer, Coordinate coordinate) {
		final int winPos = sharedStorage
				.getCoordinateController().getCoordinateTranslator()
				.coordinate2windowPosition(coordinate);
		for (final Storage cache : storages) {
			cache.populate(dataContainer, winPos, coordinate);
		}
	}
	
	public void clear() {
		sharedStorage.clear();
		for (final Storage cache : storages) {
			cache.clear();
		}
	}

	@Override
	public List<RecordExtendedPrePostProcessor> getRecordProcessors() {
		return recordProcessors;
	}
	
	@Override
	public List<Storage> getStorages() {
		return storages;
	}
	
}
