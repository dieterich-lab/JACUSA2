package jacusa.filter.factory.distance.lrtarrest;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Option.Builder;

import jacusa.filter.cache.processrecord.ProcessDeletionOperator;
import jacusa.filter.cache.processrecord.ProcessInsertionOperator;
import jacusa.filter.cache.processrecord.ProcessRecord;
import jacusa.filter.cache.processrecord.ProcessSkippedOperator;
import jacusa.method.rtarrest.RTarrestMethod.RT_READS;
import lib.data.cache.fetcher.FilteredDataFetcher;
import lib.data.cache.fetcher.basecall.Apply2readsBaseCallCountSwitch;
import lib.data.cache.lrtarrest.ArrestPos2BaseCallCount;
import lib.data.cache.region.RegionDataCache;
import lib.data.filter.ArrestPos2BaseCallCountFilteredData;

/**
 * TODO add comments.
 * 
 * @param 
 */

public class LRTarrestCombinedFilterFactory 
extends AbstractLRTarrestDistanceFilterFactory {

	public LRTarrestCombinedFilterFactory(
			final Apply2readsBaseCallCountSwitch bccSwitch, 
			final FilteredDataFetcher<ArrestPos2BaseCallCountFilteredData, ArrestPos2BaseCallCount> filteredDataFetcher) {
		
		super(
				getOptionBuilder().build(),
				bccSwitch, filteredDataFetcher,
				6, 0.5);
		getApply2Reads().add(RT_READS.ARREST);
	}

	@Override
	protected List<ProcessRecord> createProcessRecord(RegionDataCache regionDataCache) {
		return Arrays.asList(
				// INDELs		
				new ProcessInsertionOperator(getFilterDistance(), regionDataCache),
				new ProcessDeletionOperator(getFilterDistance(), regionDataCache),
				// introns
				new ProcessSkippedOperator(getFilterDistance(), regionDataCache) );
	}

	public static Builder getOptionBuilder() {
		return Option.builder(Character.toString('D'))
				.desc("Filter artefacts in the vicinity of INDELs, and splice site position(s)");
	}
	
}
