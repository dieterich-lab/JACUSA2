package jacusa.filter.factory.basecall;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Option.Builder;

import jacusa.filter.cache.processrecord.ProcessDeletionOperator;
import jacusa.filter.cache.processrecord.ProcessInsertionOperator;
import jacusa.filter.cache.processrecord.ProcessReadStartEnd;
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
 * 
 * @param <T>
 */
public class CombinedFilterFactory<T extends AbstractData & HasBaseCallCount & HasBaseCallCountFilterData & HasReferenceBase> 
extends AbstractBaseCallCountFilterFactory<T> {

	public CombinedFilterFactory() {
		super(getOptionBuilder().build(),
				new DefaultBaseCallCountExtractor<T>(), 
				6, 0.5);
	}

	@Override
	protected List<ProcessRecord> createProcessRecord(RegionDataCache<T> regionDataCache) {
		final List<ProcessRecord> processRecords = new ArrayList<ProcessRecord>(1);
		// INDELs
		processRecords.add(new ProcessInsertionOperator(getDistance(), regionDataCache));
		processRecords.add(new ProcessDeletionOperator(getDistance(), regionDataCache));
		// read start end 
		processRecords.add(new ProcessReadStartEnd(getDistance(), regionDataCache));
		// introns
		processRecords.add(new ProcessSkippedOperator(getDistance(), regionDataCache));
		return processRecords;
	}

	public static Builder getOptionBuilder() {
		return Option.builder(Character.toString('D'))
				.desc("Filter artefacts in the vicinity of read start/end, INDELs, and splice site position(s)");
	}
	
}
