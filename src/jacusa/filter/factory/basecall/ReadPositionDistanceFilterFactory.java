package jacusa.filter.factory.basecall;

import java.util.ArrayList;
import java.util.List;

import jacusa.filter.cache.processrecord.ProcessReadStartEnd;
import jacusa.filter.cache.processrecord.ProcessRecord;
import lib.data.AbstractData;
import lib.data.cache.extractor.basecall.DefaultBaseCallCountExtractor;
import lib.data.cache.region.RegionDataCache;
import lib.data.has.HasBaseCallCount;
import lib.data.has.HasReferenceBase;
import lib.data.has.filter.HasBaseCallCountFilterData;

public class ReadPositionDistanceFilterFactory<T extends AbstractData & HasBaseCallCount & HasReferenceBase & HasBaseCallCountFilterData> 
extends AbstractBaseCallCountFilterFactory<T> {

	public ReadPositionDistanceFilterFactory() {
		super('B', "Filter potential false positive variants adjacent to read start/end.", 
				new DefaultBaseCallCountExtractor<T>(),
				6, 0.5);
	}

	@Override
	protected List<ProcessRecord> createProcessRecord(final RegionDataCache<T> regionDataCache) {
		final List<ProcessRecord> processRecords = new ArrayList<ProcessRecord>(1);
		processRecords.add(new ProcessReadStartEnd(getDistance(), regionDataCache));
		return processRecords;
	}
	
}