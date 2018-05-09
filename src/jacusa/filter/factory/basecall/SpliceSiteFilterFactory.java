	package jacusa.filter.factory.basecall;

import java.util.ArrayList;
import java.util.List;

import jacusa.filter.cache.processrecord.ProcessRecord;
import jacusa.filter.cache.processrecord.ProcessSkippedOperator;
import lib.data.AbstractData;
import lib.data.cache.extractor.basecall.DefaultBaseCallCountExtractor;
import lib.data.cache.region.RegionDataCache;
import lib.data.has.HasBaseCallCount;
import lib.data.has.HasReferenceBase;
import lib.data.has.filter.HasBaseCallCountFilterData;

/**
 * TODO add comments.
 */
public class SpliceSiteFilterFactory<T extends AbstractData & HasBaseCallCount & HasReferenceBase & HasBaseCallCountFilterData>
extends AbstractBaseCallCountFilterFactory<T> {

	public SpliceSiteFilterFactory() {
		super('S', "Filter potential false positive variants adjacent to splice site(s).",
				new DefaultBaseCallCountExtractor<T>(),
				6, 0.5);
	}
	
	@Override
	protected List<ProcessRecord> createProcessRecord(final RegionDataCache<T> regionDataCache) {
		final List<ProcessRecord> processRecords = new ArrayList<ProcessRecord>(1);
		processRecords.add(new ProcessSkippedOperator(getDistance(), regionDataCache));
		return processRecords;
	}
	
	
	
	
}