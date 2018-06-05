package jacusa.filter.factory.distance.lrtarrest;

import java.util.ArrayList;

import java.util.List;

import org.apache.commons.cli.Option;

import jacusa.filter.cache.processrecord.ProcessRecord;
import jacusa.filter.cache.processrecord.ProcessSkippedOperator;
import lib.data.AbstractData;
import lib.data.cache.extractor.lrtarrest.DefaultRefPos2BaseCallCountExtractor;
import lib.data.cache.region.RegionDataCache;
import lib.data.has.HasBaseCallCount;
import lib.data.has.HasLRTarrestCount;
import lib.data.has.HasReferenceBase;
import lib.data.has.filter.HasRefPos2BaseCallCountFilterData;

/**
 * TODO add comments.
 * 
 * @param <T>
 */
public class LRTarrestSpliceSiteFilterFactory<T extends AbstractData & HasBaseCallCount & HasReferenceBase & HasLRTarrestCount & HasRefPos2BaseCallCountFilterData> 
extends AbstractLRTarrestFilterFactory<T> {

	public LRTarrestSpliceSiteFilterFactory() {
		super(Option.builder(Character.toString('S'))
				.desc("Filter artefacts around splice site of read arrest positions.")
				.build(),
				new DefaultRefPos2BaseCallCountExtractor<T>(),
				6, 0.5);
	}

	@Override
	protected List<ProcessRecord> createProcessRecord(RegionDataCache<T> regionDataCache) {
		final List<ProcessRecord> processRecords = new ArrayList<ProcessRecord>(1);
		// introns
		processRecords.add(new ProcessSkippedOperator(getDistance(), regionDataCache));
		return processRecords;
	}
	
}