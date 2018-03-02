package jacusa.filter.cache;

import jacusa.filter.cache.processrecord.ProcessRecord;

import java.util.List;

import lib.data.AbstractData;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.cache.AbstractUniqueDataCache;
import lib.util.coordinate.CoordinateController;
import lib.util.coordinate.Coordinate;

public class UniqueFilterCacheWrapper<T extends AbstractData> 
extends AbstractFilterCache<T> {

	private AbstractUniqueDataCache<T> uniqueDataCache;
	private List<ProcessRecord> processRecord;

	public UniqueFilterCacheWrapper(final char c, 
			final AbstractUniqueDataCache<T> uniqueDataCache,
			final List<ProcessRecord> processRecord) {

		super(c);
		this.uniqueDataCache = uniqueDataCache;
		this.processRecord = processRecord;
	}

	@Override
	public void addRecordWrapper(final SAMRecordWrapper recordWrapper) {
		uniqueDataCache.resetVisited(recordWrapper); 
		for (final ProcessRecord p : processRecord) {
			p.processRecord(recordWrapper);
		}
	}
	
	@Override
	public CoordinateController getCoordinateController() {
		return  uniqueDataCache.getCoordinateController();
	}
	
	@Override
	public void clear() {
		uniqueDataCache.clear();	
	}

	@Override
	public void addData(T data, Coordinate coordinate) {
		uniqueDataCache.addData(data, coordinate);
	}

}