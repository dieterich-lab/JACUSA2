package jacusa.filter.factory.basecall;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.Option;

import jacusa.filter.cache.processrecord.ProcessReadStartEnd;
import jacusa.filter.cache.processrecord.ProcessRecord;
import lib.data.cache.fetcher.Fetcher;
import lib.data.cache.fetcher.FilteredDataFetcher;
import lib.data.cache.region.RegionDataCache;
import lib.data.count.basecall.BaseCallCount;
import lib.data.filter.BaseCallCountFilteredData;

public class ReadPositionDistanceFilterFactory 
extends AbstractBaseCallCountFilterFactory {

	public ReadPositionDistanceFilterFactory(
			final Fetcher<BaseCallCount> observedBccFetcher,
			final FilteredDataFetcher<BaseCallCountFilteredData, BaseCallCount> filteredDataFetcher) {

		super(
				Option.builder(Character.toString('B'))
					.desc("Filter potential false positive variants adjacent to read start/end.")
					.build(), 
				observedBccFetcher, filteredDataFetcher,
				6, 0.5);
	}

	@Override
	protected List<ProcessRecord> createProcessRecord(final RegionDataCache regionDataCache) {
		final List<ProcessRecord> processRecords = new ArrayList<ProcessRecord>(1);
		processRecords.add(new ProcessReadStartEnd(getFilterDistance(), regionDataCache));
		return processRecords;
	}
	
}