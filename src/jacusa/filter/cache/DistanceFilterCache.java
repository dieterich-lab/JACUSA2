package jacusa.filter.cache;

import jacusa.filter.cache.processrecord.ProcessRecord;

import java.util.List;

import lib.data.AbstractData;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.cache.UniqueBaseCallDataCache;
import lib.data.has.hasBaseCallCount;
import lib.util.coordinate.CoordinateController;
import lib.util.coordinate.Coordinate;

public class DistanceFilterCache<T extends AbstractData & hasBaseCallCount> 
extends AbstractFilterCache<T> {

	private UniqueBaseCallDataCache<T> uniqueBaseCallCache;
	private List<ProcessRecord> processRecord;
	
	public DistanceFilterCache(final char c, 
			final UniqueBaseCallDataCache<T> uniqueBaseCallCache,
			final List<ProcessRecord> processRecord) {

		super(c);
		this.uniqueBaseCallCache = uniqueBaseCallCache;
		this.processRecord = processRecord;
	}

	@Override
	public void addRecordWrapper(final SAMRecordWrapper recordWrapper) {
		uniqueBaseCallCache.resetVisited(recordWrapper); 
		for (final ProcessRecord p : processRecord) {
			p.processRecord(recordWrapper);
		}
	}
	
	@Override
	public CoordinateController getCoordinateController() {
		return  uniqueBaseCallCache.getCoordinateController();
	}
	
	@Override
	public void clear() {
		uniqueBaseCallCache.clear();	
	}

	@Override
	public void addData(T data, Coordinate coordinate) {
		uniqueBaseCallCache.addData(data, coordinate);
	}
}