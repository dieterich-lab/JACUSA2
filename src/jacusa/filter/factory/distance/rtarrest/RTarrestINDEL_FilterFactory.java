package jacusa.filter.factory.distance.rtarrest;

import java.util.ArrayList;
import java.util.List;

import jacusa.filter.cache.processrecord.ProcessDeletionOperator;
import jacusa.filter.cache.processrecord.ProcessInsertionOperator;
import jacusa.filter.cache.processrecord.ProcessRecord;
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
 * 
 * @param <T>
 */
public class RTarrestINDEL_FilterFactory<T extends AbstractData & HasBaseCallCount & HasArrestBaseCallCount & HasThroughBaseCallCount & HasBaseCallCountFilterData & HasReferenceBase> 
extends AbstractRTarrestDistanceFilterFactory<T> {

	public RTarrestINDEL_FilterFactory() {
		super('I', "Filter potential false positive variants adjacent to INDEL position(s)",
				new ArrestBaseCallCountExtractor<T>(),
				6, 0.5);
	}

	@Override
	protected List<ProcessRecord> createProcessRecord(RegionDataCache<T> regionDataCache) {
		final List<ProcessRecord> processRecords = new ArrayList<ProcessRecord>(1);
		processRecords.add(new ProcessInsertionOperator(getDistance(), regionDataCache));
		processRecords.add(new ProcessDeletionOperator(getDistance(), regionDataCache));
		return processRecords;
	}

}