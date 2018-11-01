package jacusa.filter.factory.distance.rtarrest;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Option.Builder;

import jacusa.filter.cache.processrecord.ProcessDeletionOperator;
import jacusa.filter.cache.processrecord.ProcessInsertionOperator;
import jacusa.filter.cache.processrecord.ProcessReadStartEnd;
import jacusa.filter.cache.processrecord.ProcessRecord;
import jacusa.filter.cache.processrecord.ProcessSkippedOperator;
import jacusa.method.rtarrest.RTarrestMethod.RT_READS;
import lib.data.cache.fetcher.FilteredDataFetcher;
import lib.data.cache.fetcher.basecall.Apply2readsBaseCallCountSwitch;
import lib.data.cache.region.RegionDataCache;
import lib.data.count.basecall.BaseCallCount;
import lib.data.filter.BaseCallCountFilteredData;

/**
 * TODO add comments.
 * 
 * @param 
 */

public class RTarrestCombinedFilterFactory 
extends AbstractRTarrestDistanceFilterFactory {

	public RTarrestCombinedFilterFactory(
			final Apply2readsBaseCallCountSwitch bccSwitch, 
			final FilteredDataFetcher<BaseCallCountFilteredData, BaseCallCount> filteredDataFetcher) {

		super(
				getOptionBuilder().build(),
				bccSwitch, filteredDataFetcher,
				6, 0.5);

		getApply2Reads().add(RT_READS.ARREST);
	}

	@Override
	protected List<ProcessRecord> createProcessRecord(RegionDataCache regionDataCache) {
		final List<ProcessRecord> processRecords = new ArrayList<ProcessRecord>(1);
		// INDELs
		processRecords.add(new ProcessInsertionOperator(getFilterDistance(), regionDataCache));
		processRecords.add(new ProcessDeletionOperator(getFilterDistance(), regionDataCache));
		// read start end 
		processRecords.add(new ProcessReadStartEnd(getFilterDistance(), regionDataCache));
		// introns
		processRecords.add(new ProcessSkippedOperator(getFilterDistance(), regionDataCache));
		return processRecords;
	}

	public static Builder getOptionBuilder() {
		return Option.builder(Character.toString('D'))
				.desc("Filter artefacts in the vicinity of INDELs, and splice site position(s)");
	}
	
}
