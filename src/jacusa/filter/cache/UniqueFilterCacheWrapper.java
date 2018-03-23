package jacusa.filter.cache;

import jacusa.filter.cache.processrecord.ProcessRecord;

import java.util.List;

import lib.data.AbstractData;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.cache.region.UniqueRegionDataCache;
import lib.util.coordinate.CoordinateController;
import lib.util.coordinate.Coordinate;

/**
 * TODO add comments.
 * 
 * @param <T>
 */
public class UniqueFilterCacheWrapper<T extends AbstractData> 
extends AbstractFilterCache<T> {

	private UniqueRegionDataCache<T> uniqueRegionDataCache;
	private List<ProcessRecord> processRecord;

	public UniqueFilterCacheWrapper(final char c, 
			final UniqueRegionDataCache<T> uniqueRegionDataCache,
			final List<ProcessRecord> processRecord) {

		super(c);
		this.uniqueRegionDataCache = uniqueRegionDataCache;
		this.processRecord = processRecord;
	}

	@Override
	public void addRecordWrapper(final SAMRecordWrapper recordWrapper) {
		uniqueRegionDataCache.resetVisited(recordWrapper); 
		for (final ProcessRecord p : processRecord) {
			p.processRecord(recordWrapper);
		}
	}

	@Override
	public CoordinateController getCoordinateController() {
		return  uniqueRegionDataCache.getCoordinateController();
	}

	@Override
	public void clear() {
		uniqueRegionDataCache.clear();	
	}

	@Override
	public void addData(T data, Coordinate coordinate) {
		uniqueRegionDataCache.addData(data, coordinate);
	}

}