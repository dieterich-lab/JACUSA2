	package jacusa.filter.factory.basecall;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Option.Builder;

import jacusa.filter.cache.processrecord.ProcessRecord;
import jacusa.filter.cache.processrecord.ProcessSkippedOperator;
import lib.data.cache.fetcher.Fetcher;
import lib.data.cache.fetcher.FilteredDataFetcher;
import lib.data.cache.region.RegionDataCache;
import lib.data.count.basecall.BaseCallCount;
import lib.data.filter.BaseCallCountFilteredData;

/**
 * TODO add comments.
 */

public class SpliceSiteFilterFactory
extends AbstractBaseCallCountFilterFactory {

	public SpliceSiteFilterFactory(
			final Fetcher<BaseCallCount> observedBccFetcher,
			final FilteredDataFetcher<BaseCallCountFilteredData, BaseCallCount> filteredDataFetcher) {
		
		super(
				getOptionBuilder().build(),
				observedBccFetcher, filteredDataFetcher,
				6, 0.5);
	}
	
	@Override
	protected List<ProcessRecord> createProcessRecord(final RegionDataCache regionDataCache) {
		final List<ProcessRecord> processRecords = new ArrayList<ProcessRecord>(1);
		processRecords.add(new ProcessSkippedOperator(getFilterDistance(), regionDataCache));
		return processRecords;
	}
	
	public static Builder getOptionBuilder() {
		return Option.builder(Character.toString('S'))
				.desc("Filter potential false positive variants adjacent to splice site(s).");
	}
	
	
	
}