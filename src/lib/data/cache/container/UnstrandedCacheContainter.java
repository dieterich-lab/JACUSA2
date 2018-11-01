package lib.data.cache.container;

import java.util.List;

import lib.data.DataTypeContainer;
import lib.data.adder.DataContainerAdder;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.cache.record.RecordWrapperDataCache;
import lib.util.coordinate.Coordinate;

public class UnstrandedCacheContainter 
implements CacheContainer {

	private final SharedCache sharedCache;
	private final List<RecordWrapperDataCache> caches;
	
	public UnstrandedCacheContainter(
			final SharedCache sharedCache, 
			final List<RecordWrapperDataCache> dataCaches) {

		this.sharedCache			= sharedCache;
		this.caches 				= dataCaches;
	}
	
	@Override
	public ReferenceProvider getReferenceProvider() {
		return sharedCache.getReferenceProvider();
	}
	
	@Override
	public int getNext(final int windowPosition) {
		return sharedCache.getNext(windowPosition);
	}
	
	@Override
	public void process(final SAMRecordWrapper recordWrapper) {
		sharedCache.addRecordWrapper(recordWrapper);

		for (final RecordWrapperDataCache dataCache : caches) {
			dataCache.processRecordWrapper(recordWrapper);
		}
	}
	
	
	@Override
	public void populateContainer(DataTypeContainer container, Coordinate coordinate) {
		for (final DataContainerAdder cache : caches) {
			cache.populate(container, coordinate);
		}
	}
	
	public void clear() {
		sharedCache.clear();
		for (final DataContainerAdder dataCache : caches) {
			dataCache.clear();
		}
	}

	@Override
	public List<RecordWrapperDataCache> getCaches() {
		return caches;
	}

}
