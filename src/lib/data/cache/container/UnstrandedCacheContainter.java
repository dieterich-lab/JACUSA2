package lib.data.cache.container;

import java.util.List;

import lib.data.AbstractData;
import lib.data.adder.DataAdder;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.cache.extractor.ReferenceSetter;
import lib.data.cache.record.RecordWrapperDataCache;
import lib.util.coordinate.CoordinateController;
import lib.util.coordinate.Coordinate;

public class UnstrandedCacheContainter<T extends AbstractData> 
implements CacheContainer<T> {

	private final ReferenceSetter<T> referenceSetter; 
	private final CoordinateController coordinateController;
	private final List<RecordWrapperDataCache<T>> caches;
	
	private final SharedCache sharedCache;

	public UnstrandedCacheContainter(
			final ReferenceSetter<T> referenceSetter,
			final CoordinateController coordinateController, 
			final List<RecordWrapperDataCache<T>> dataCaches) {

		this.referenceSetter  		= referenceSetter;
		this.coordinateController	= coordinateController;
		this.caches 			= dataCaches;
		
		sharedCache					= coordinateController.getSharedCache();
	}
	
	@Override
	public int getNext(final int windowPosition) {
		return sharedCache.getNext(windowPosition);
	}
	
	@Override
	public void add(final SAMRecordWrapper recordWrapper) {
		sharedCache.addRecordWrapper(recordWrapper);

		for (final RecordWrapperDataCache<T> dataCache : caches) {
			dataCache.addRecordWrapper(recordWrapper);
		}
	}
	
	@Override
	public void addData(final T data, Coordinate coordinate) {
		referenceSetter.setReference(coordinate, data, coordinateController.getReferenceProvider());
		for (final DataAdder<T> dataCache : caches) {
			dataCache.addData(data, coordinate);
		}
	}
	
	public void clear() {
		sharedCache.clear();
		for (final DataAdder<T> dataCache : caches) {
			dataCache.clear();
		}
	}

	@Override
	public List<RecordWrapperDataCache<T>> getCaches() {
		return caches;
	}

}
