package jacusa.filter.cache;

import jacusa.filter.cache.processrecord.ProcessRecord;

import java.util.List;

import lib.data.AbstractData;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.cache.record.RecordDataCache;
import lib.data.cache.region.RegionDataCache;
import lib.util.coordinate.CoordinateController;
import lib.util.coordinate.Coordinate;

/**
 * TODO add comments.
 * 
 * @param <T>
 */
public class RecordProcessDataCache<T extends AbstractData> 
implements RecordDataCache<T> {

	private final RegionDataCache<T> regionDataCache;
	private final List<ProcessRecord> processRecord;

	public RecordProcessDataCache(
			final RegionDataCache<T> regionDataCache,
			final List<ProcessRecord> processRecord) {

		this.regionDataCache = regionDataCache;
		this.processRecord = processRecord;
	}

	@Override
	public void addRecord(final SAMRecordWrapper recordWrapper) {
		for (final ProcessRecord p : processRecord) {
			p.processRecord(recordWrapper);
		}
	}

	@Override
	public CoordinateController getCoordinateController() {
		return  regionDataCache.getCoordinateController();
	}

	@Override
	public void clear() {
		regionDataCache.clear();	
	}

	@Override
	public void addData(T data, Coordinate coordinate) {
		regionDataCache.addData(data, coordinate);
	}

}