package jacusa.filter.factory.distance.lrtarrest;

import java.util.ArrayList;

import java.util.List;

import org.apache.commons.cli.Option;

import jacusa.filter.cache.processrecord.ProcessDeletionOperator;
import jacusa.filter.cache.processrecord.ProcessInsertionOperator;
import jacusa.filter.cache.processrecord.ProcessRecord;
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
public class LRTarrestINDEL_FilterFactory<T extends AbstractData & HasBaseCallCount & HasReferenceBase & HasLRTarrestCount & HasRefPos2BaseCallCountFilterData> 
extends AbstractLRTarrestFilterFactory<T> {

	public LRTarrestINDEL_FilterFactory() {
		super(Option.builder(Character.toString('I'))
				.desc("Filter artefacts around INDELs of read arrest reads.")
				.build(),
				new DefaultRefPos2BaseCallCountExtractor<T>(),
				6, 0.5);
	}

	@Override
	protected List<ProcessRecord> createProcessRecord(RegionDataCache<T> regionDataCache) {
		final List<ProcessRecord> processRecords = new ArrayList<ProcessRecord>(1);
		// INDELs
		processRecords.add(new ProcessInsertionOperator(getDistance(), regionDataCache));
		processRecords.add(new ProcessDeletionOperator(getDistance(), regionDataCache));
		return processRecords;
	}

}