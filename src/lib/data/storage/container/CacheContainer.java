package lib.data.storage.container;

import java.util.ArrayList;
import java.util.List;

import lib.data.DataContainer;
import lib.data.storage.Cache;
import lib.data.storage.Storage;
import lib.data.storage.processor.ExtendedRecordProcessor;
import lib.record.Record;
import lib.util.LibraryType;
import lib.util.coordinate.Coordinate;

/**
 * TODO add documentation
 */
public interface CacheContainer {

	void preProcess();
	void process(Record record);
	void postProcess();
	
	int getNextWindowPosition(int winPos);
	ReferenceProvider getReferenceProvider();
	
	void populate(DataContainer dataContainer, Coordinate coordinate);
	
	List<ExtendedRecordProcessor> getRecordProcessors();
	List<Storage> getStorages();
	
	default void clearStorage() {
		for (final Storage storage : getStorages()) {
			storage.clear();
		}
	}
	void clearSharedStorage();
	
	public static class StrandedBuilder implements lib.util.Builder<CacheContainer> {
		
		private final LibraryType libraryType;
		private final SharedStorage sharedStorage;

		private List<ExtendedRecordProcessor> recordProcessors1;
		private List<ExtendedRecordProcessor> recordProcessors2;
		
		private List<Storage> storages1;
		private List<Storage> storages2;
		
		public StrandedBuilder(final LibraryType libraryType, final SharedStorage sharedStorage) {
			switch (libraryType) {
			case UNSTRANDED:
			case MIXED:
				throw new IllegalArgumentException("Unsupported library type: " + libraryType.toString());
				
			default:
				this.libraryType = libraryType;
			}			
			
			this.sharedStorage = sharedStorage;
			
			recordProcessors1 	= new ArrayList<>();
			recordProcessors2 	= new ArrayList<>();
			
			storages1 			= new ArrayList<>();
			storages2 			= new ArrayList<>();
		}
		
		public StrandedBuilder withStorage(final Storage storage1, final Storage storage2) {
			storages1.add(storage1);
			storages2.add(storage2);
			return this;
		}
		
		@Override
		public CacheContainer build() {
			final Cache cache1 = new Cache();
			cache1.addStorages(storages1);
			cache1.addRecordProcessors(recordProcessors1);
			
			final Cache cache2 = new Cache();
			cache2.addStorages(storages2);
			cache2.addRecordProcessors(recordProcessors2);
			
			switch (libraryType) {
			case RF_FIRSTSTRAND:
				return new RFPairedEnd1CacheContainer(
						new UnstrandedCacheContainter(sharedStorage, cache1),
						new UnstrandedCacheContainter(sharedStorage, cache2) );
			case FR_SECONDSTRAND:
				return new FRPairedEnd2CacheContainer(
						new UnstrandedCacheContainter(sharedStorage, cache1),
						new UnstrandedCacheContainter(sharedStorage, cache2) );

			default:
				throw new IllegalArgumentException("Unsupported library type: " + libraryType.toString());
			}
		}
		
	}

}
