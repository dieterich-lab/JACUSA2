package lib.data.cache.container;

import java.util.ArrayList;
import java.util.List;

import lib.data.DataTypeContainer;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.cache.record.RecordWrapperProcessor;
import lib.data.has.LibraryType;
import lib.util.coordinate.Coordinate;

public interface CacheContainer {

	void preProcess();
	void process(final SAMRecordWrapper recordWrapper);
	void postProcess();
	
	int getNext(final int windowPosition);
	ReferenceProvider getReferenceProvider();
	
	void populate(DataTypeContainer container, final Coordinate coordinate);
	
	List<RecordWrapperProcessor> getCaches();
	void clear();
	
	public static class Builder implements lib.util.Builder<CacheContainer> {
		
		private final LibraryType libraryType;
		private final SharedCache sharedCache;

		private List<RecordWrapperProcessor> caches;
		
		public Builder(final LibraryType libraryType, final SharedCache sharedCache) {
			this.libraryType = libraryType;
			this.sharedCache = sharedCache;
			
			caches = new ArrayList<RecordWrapperProcessor>();
		}
		
		public Builder withCache(final RecordWrapperProcessor cache) {
			caches.add(cache);
			return this;
		}
		
		public Builder withCache(final List<RecordWrapperProcessor> caches) {
			caches.addAll(caches);
			return this;
		}
		
		@Override
		public CacheContainer build() {
			switch (libraryType) {
			case UNSTRANDED:
				return new UnstrandedCacheContainter(sharedCache, caches);
				
			case RF_FIRSTSTRAND:
				return new RFPairedEnd1CacheContainer(
						new UnstrandedCacheContainter(sharedCache, caches),
						new UnstrandedCacheContainter(sharedCache, caches) );
				
			case FR_SECONDSTRAND:
				return new FRPairedEnd2CacheContainer(
						new UnstrandedCacheContainter(sharedCache, caches),
						new UnstrandedCacheContainter(sharedCache, caches) );

			default:
				throw new IllegalArgumentException("Unsupported library type: " + libraryType.toString());
			}
		}
		
	}
	
}
