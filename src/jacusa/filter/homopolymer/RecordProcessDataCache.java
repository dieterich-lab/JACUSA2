package jacusa.filter.homopolymer;

import jacusa.filter.cache.processrecord.ProcessRecord;

import java.util.List;

import lib.data.DataTypeContainer;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.cache.record.RecordWrapperProcessor;
import lib.data.cache.region.RegionDataCache;
import lib.util.coordinate.Coordinate;

/**
 * TODO add comments.
 * 
 * @param 
 */
public class RecordProcessDataCache 
implements RecordWrapperProcessor {

	private final RegionDataCache regionDataCache;
	private final List<ProcessRecord> processRecord;

	public RecordProcessDataCache(
			final RegionDataCache regionDataCache,
			final List<ProcessRecord> processRecord) {

		this.regionDataCache = regionDataCache;
		this.processRecord = processRecord;
	}

	@Override
	public void preProcess() {
		// nothing to be done
	}
	
	@Override
	public void process(final SAMRecordWrapper recordWrapper) {
		for (final ProcessRecord p : processRecord) {
			p.processRecord(recordWrapper);
		}
	}

	@Override
	public void postProcess() {
		// nothing to be done
	}
	
	@Override
	public void clear() {
		regionDataCache.clear();	
	}

	@Override
	public void populate(DataTypeContainer container, Coordinate coordinate) {
		regionDataCache.populate(container, coordinate);
	}

}