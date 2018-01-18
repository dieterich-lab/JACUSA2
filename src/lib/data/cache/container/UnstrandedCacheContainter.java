package lib.data.cache.container;

import java.util.List;

import lib.data.AbstractData;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.cache.DataCache;
import lib.tmp.CoordinateController;
import lib.util.coordinate.Coordinate;

public class UnstrandedCacheContainter<T extends AbstractData> 
implements CacheContainer<T> {

	private final GeneralCache generalCache;
	
	private final List<DataCache<T>> dataCaches;

	public UnstrandedCacheContainter(final CoordinateController coordinateController, final List<DataCache<T>> dataCaches) {
		generalCache = coordinateController.getGeneralCache();
		this.dataCaches = dataCaches;
	}
	
	@Override
	public int getNext(final int windowPosition) {
		return generalCache.getNext(windowPosition);
	}
	
	@Override
	public void addRecordWrapper(final SAMRecordWrapper recordWrapper) {
		generalCache.addRecordWrapper(recordWrapper);
		
		for (final DataCache<T> dataCache : dataCaches) {
			dataCache.addRecordWrapper(recordWrapper);
		}
	}
	
	@Override
	public void addData(final T data, Coordinate coordinate) {
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
	public List<DataCache<T>> getDataCaches() {
		return dataCaches;
	}

}
