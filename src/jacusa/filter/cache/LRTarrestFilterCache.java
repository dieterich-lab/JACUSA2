package jacusa.filter.cache;

import jacusa.filter.cache.processrecord.ProcessRecord;

import java.util.List;

import lib.data.AbstractData;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.cache.UniqueRef2BaseCallDataCache;
import lib.data.has.hasLRTarrestCount;
import lib.data.has.hasReferenceBase;
import lib.util.coordinate.CoordinateController;
import lib.util.coordinate.Coordinate;


/**
 * TODO add comments
 * // TODO merge unique caches
 * @param <T>
 */
public class LRTarrestFilterCache<T extends AbstractData & hasReferenceBase & hasLRTarrestCount> 
extends AbstractFilterCache<T> {

	private UniqueRef2BaseCallDataCache<T> uniqueRef2BaseCallDataCache;
	private List<ProcessRecord> processRecord;

	public LRTarrestFilterCache(final char c, 
			final UniqueRef2BaseCallDataCache<T> uniqueRef2BaseCallDataCache,
			final List<ProcessRecord> processRecord) {

		super(c);
		this.uniqueRef2BaseCallDataCache = uniqueRef2BaseCallDataCache;
		this.processRecord = processRecord;
	}

	@Override
	public void addRecordWrapper(final SAMRecordWrapper recordWrapper) {
		uniqueRef2BaseCallDataCache.resetVisited(recordWrapper); 
		for (final ProcessRecord p : processRecord) {
			p.processRecord(recordWrapper);
		}
	}
	
	@Override
	public CoordinateController getCoordinateController() {
		return  uniqueRef2BaseCallDataCache.getCoordinateController();
	}
	
	@Override
	public void clear() {
		uniqueRef2BaseCallDataCache.clear();	
	}

	@Override
	public void addData(T data, Coordinate coordinate) {
		uniqueRef2BaseCallDataCache.addData(data, coordinate);
	}

}