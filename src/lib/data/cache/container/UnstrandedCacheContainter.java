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
	private final List<RecordWrapperDataCache<T>> dataCaches;
	
	private final GeneralCache generalCache;

	public UnstrandedCacheContainter(
			final ReferenceSetter<T> referenceSetter,
			final CoordinateController coordinateController, 
			final List<RecordWrapperDataCache<T>> dataCaches) {
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

		for (final RecordWrapperDataCache<T> dataCache : dataCaches) {
			dataCache.addRecordWrapper(recordWrapper);
		}
	}
	
	@Override
	public void addData(final T data, Coordinate coordinate) {
		referenceSetter.setReference(coordinate, data, coordinateController.getReferenceProvider());
		for (final DataAdder<T> dataCache : dataCaches) {
			dataCache.addData(data, coordinate);
		}
	}
	
	public void clear() {
		generalCache.clear();
		for (final DataAdder<T> dataCache : dataCaches) {
			dataCache.clear();
		}
	}

	@Override
	public List<RecordWrapperDataCache<T>> getDataCaches() {
		return dataCaches;
	}

}
