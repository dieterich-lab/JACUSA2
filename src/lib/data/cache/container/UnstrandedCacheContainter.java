package lib.data.cache.container;

import java.util.List;

import lib.data.AbstractData;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.cache.DataCache;
import lib.data.cache.extractor.ReferenceSetter;
import lib.data.cache.record.RecordDataCache;
import lib.util.coordinate.CoordinateController;
import lib.util.coordinate.Coordinate;

public class UnstrandedCacheContainter<T extends AbstractData> 
implements CacheContainer<T> {

	private final ReferenceSetter<T> referenceSetter; 
	private final CoordinateController coordinateController;
	private final List<RecordDataCache<T>> dataCaches;
	
	private final GeneralCache generalCache;

	public UnstrandedCacheContainter(
			final ReferenceSetter<T> referenceSetter,
			final CoordinateController coordinateController, 
			final List<RecordDataCache<T>> dataCaches) {
		this.referenceSetter  		= referenceSetter;
		this.coordinateController	= coordinateController;
		this.dataCaches 			= dataCaches;
		
		generalCache				= coordinateController.getGeneralCache();
	}
	
	@Override
	public int getNext(final int windowPosition) {
		return generalCache.getNext(windowPosition);
	}
	
	@Override
	public void add(final SAMRecordWrapper recordWrapper) {
		generalCache.addRecordWrapper(recordWrapper);

		for (final RecordDataCache<T> dataCache : dataCaches) {
			dataCache.addRecord(recordWrapper);
		}
	}
	
	@Override
	public void addData(final T data, Coordinate coordinate) {
		referenceSetter.setReference(coordinate, data, coordinateController.getReferenceProvider());
		for (final DataCache<T> dataCache : dataCaches) {
			dataCache.addData(data, coordinate);
		}
	}
	
	public void clear() {
		generalCache.clear();
		for (final DataCache<T> dataCache : dataCaches) {
			dataCache.clear();
		}
	}

	@Override
	public List<RecordDataCache<T>> getDataCaches() {
		return dataCaches;
	}

}
