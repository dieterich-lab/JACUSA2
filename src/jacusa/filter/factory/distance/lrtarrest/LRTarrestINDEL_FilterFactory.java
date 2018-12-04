package jacusa.filter.factory.distance.lrtarrest;

import java.util.ArrayList;
import java.util.List;

import jacusa.filter.cache.processrecord.ProcessDeletionOperator;
import jacusa.filter.cache.processrecord.ProcessInsertionOperator;
import jacusa.filter.cache.processrecord.ProcessRecord;
import jacusa.filter.factory.basecall.INDEL_FilterFactory;
import lib.data.cache.fetcher.FilteredDataFetcher;
import lib.data.cache.fetcher.basecall.Apply2readsBaseCallCountSwitch;
import lib.data.cache.lrtarrest.ArrestPosition2baseCallCount;
import lib.data.cache.region.RegionDataCache;
import lib.data.filter.ArrestPos2BaseCallCountFilteredData;

/**
 * TODO add comments.
 * 
 * @param 
 */

public class LRTarrestINDEL_FilterFactory 
extends AbstractLRTarrestDistanceFilterFactory {

	public LRTarrestINDEL_FilterFactory(
			final Apply2readsBaseCallCountSwitch bccSwitch, 
			final FilteredDataFetcher<ArrestPos2BaseCallCountFilteredData, ArrestPosition2baseCallCount> filteredDataFetcher) {
		
		super(
				INDEL_FilterFactory.getOptionBuilder().build(),
				bccSwitch, filteredDataFetcher,
				6, 0.5);
	}
	

	@Override
	protected List<ProcessRecord> createProcessRecord(RegionDataCache regionDataCache) {
		final List<ProcessRecord> processRecords = new ArrayList<ProcessRecord>(1);
		processRecords.add(new ProcessInsertionOperator(getFilterDistance(), regionDataCache));
		processRecords.add(new ProcessDeletionOperator(getFilterDistance(), regionDataCache));
		return processRecords;
	}

}