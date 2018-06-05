	package jacusa.filter.factory.distance.rtarrest;

import java.util.ArrayList;
import java.util.List;

import jacusa.filter.cache.processrecord.ProcessReadStartEnd;
import jacusa.filter.cache.processrecord.ProcessRecord;
import jacusa.filter.factory.basecall.SpliceSiteFilterFactory;
import jacusa.method.rtarrest.RTArrestFactory.RT_READS;
import lib.data.AbstractData;
import lib.data.cache.extractor.basecall.ArrestBaseCallCountExtractor;
import lib.data.cache.region.RegionDataCache;
import lib.data.has.HasArrestBaseCallCount;
import lib.data.has.HasBaseCallCount;
import lib.data.has.HasReferenceBase;
import lib.data.has.HasThroughBaseCallCount;
import lib.data.has.filter.HasBaseCallCountFilterData;

/**
 * TODO add comments.
 */
public class RTarrestSpliceSiteFilterFactory<T extends AbstractData & HasBaseCallCount & HasArrestBaseCallCount & HasThroughBaseCallCount & HasReferenceBase & HasBaseCallCountFilterData>
extends AbstractRTarrestDistanceFilterFactory<T> {

	public RTarrestSpliceSiteFilterFactory() {
		super(SpliceSiteFilterFactory.getOptionBuilder().build(),
				new ArrestBaseCallCountExtractor<T>(),
				6, 0.5);
		getApply2Reads().add(RT_READS.ARREST);
	}
	
	@Override
	protected List<ProcessRecord> createProcessRecord(RegionDataCache<T> regionDataCache) {
		final List<ProcessRecord> processRecords = new ArrayList<ProcessRecord>(1);
		processRecords.add(new ProcessReadStartEnd(getDistance(), regionDataCache));
		return processRecords;
	}

}