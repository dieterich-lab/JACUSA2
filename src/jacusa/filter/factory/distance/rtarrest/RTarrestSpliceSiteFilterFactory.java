package jacusa.filter.factory.distance.rtarrest;

import java.util.Arrays;
import java.util.List;

import jacusa.filter.cache.processrecord.ProcessReadStartEnd;
import jacusa.filter.cache.processrecord.ProcessRecord;
import jacusa.filter.factory.basecall.SpliceSiteFilterFactory;
import lib.data.cache.fetcher.FilteredDataFetcher;
import lib.data.cache.fetcher.basecall.Apply2readsBaseCallCountSwitch;
import lib.data.cache.region.RegionDataCache;
import lib.data.count.basecall.BaseCallCount;
import lib.data.filter.BaseCallCountFilteredData;

/**
 * TODO add comments.
 */

public class RTarrestSpliceSiteFilterFactory
extends AbstractRTarrestDistanceFilterFactory {

	public RTarrestSpliceSiteFilterFactory(
			final Apply2readsBaseCallCountSwitch bccSwitch, 
			final FilteredDataFetcher<BaseCallCountFilteredData, BaseCallCount> filteredDataFetcher) {

		super(
				SpliceSiteFilterFactory.getOptionBuilder().build(),
				bccSwitch, filteredDataFetcher,
				6, 0.5);
	}
	
	@Override
	protected List<ProcessRecord> createProcessRecord(RegionDataCache regionDataCache) {
		return Arrays.asList(
				new ProcessReadStartEnd(getFilterDistance(), regionDataCache));
	}

}