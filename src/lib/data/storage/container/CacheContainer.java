package lib.data.storage.container;

import java.util.ArrayList;
import java.util.List;

import lib.data.DataContainer;
import lib.data.storage.Cache;
import lib.data.storage.processor.RecordExtendedPrePostProcessor;
import lib.data.stroage.Storage;
import lib.util.LibraryType;
import lib.util.coordinate.Coordinate;
import lib.recordextended.SAMRecordExtended;

public interface CacheContainer {

	void preProcess();
	void process(SAMRecordExtended recordExtended);
	void postProcess();
	
	int getNextWindowPosition(int winPos);
	ReferenceProvider getReferenceProvider();
	
	void populate(DataContainer dataContainer, Coordinate coordinate);
	
	List<RecordExtendedPrePostProcessor> getRecordProcessors();
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

		private List<RecordExtendedPrePostProcessor> recordProcessors1;
		private List<RecordExtendedPrePostProcessor> recordProcessors2;
		
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
			
			recordProcessors1 	= new ArrayList<RecordExtendedPrePostProcessor>();
			recordProcessors2 	= new ArrayList<RecordExtendedPrePostProcessor>();
			
			storages1 			= new ArrayList<>();
			storages2 			= new ArrayList<>();
		}

		public StrandedBuilder withRecordProcessors(
				final List<RecordExtendedPrePostProcessor> recordProcessors1,
				final List<RecordExtendedPrePostProcessor> recordProcessors2) {
			
			recordProcessors1.addAll(recordProcessors1);
			recordProcessors2.addAll(recordProcessors2);
			return this;
		}
		
		public StrandedBuilder withStorage(final Storage storage1, final Storage storage2) {
			storages1.add(storage1);
			storages2.add(storage2);
			return this;
		}
		
		public StrandedBuilder withStorages(
				final List<Storage> storages1, final List<Storage> storages2) {
			storages1.addAll(storages1);
			storages2.addAll(storages2);
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
	
	public static class UnstrandedBuilder implements lib.util.Builder<CacheContainer> {
		
		private final SharedStorage sharedStorage;

		private List<RecordExtendedPrePostProcessor> recordProcessors;
		private List<Storage> storages;
		
		public UnstrandedBuilder(final SharedStorage sharedStorage) {
			this.sharedStorage = sharedStorage;
			
			recordProcessors 	= new ArrayList<RecordExtendedPrePostProcessor>();
			storages 				= new ArrayList<>();
		}
		
		public UnstrandedBuilder withProcessor(final RecordExtendedPrePostProcessor processor) {
			recordProcessors.add(processor);
			return this;
		}
		
		public UnstrandedBuilder withRecordProcessors(final List<RecordExtendedPrePostProcessor> recordProcessors) {
			recordProcessors.addAll(recordProcessors);
			return this;
		}
		
		public UnstrandedBuilder withCache(final Storage cache) {
			storages.add(cache);
			return this;
		}
		
		public UnstrandedBuilder withCaches(final List<Storage> caches) {
			caches.addAll(caches);
			return this;
		}
		
		@Override
		public CacheContainer build() {
			final Cache cache = new Cache();
			cache.addStorages(storages);
			return new UnstrandedCacheContainter(sharedStorage, cache);
		}
		
	}
	
}
