package jacusa.filter.factory.distance.rtarrest;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.Option;

import jacusa.filter.cache.processrecord.ProcessReadStartEnd;
import jacusa.filter.cache.processrecord.ProcessRecord;
import jacusa.method.rtarrest.RTarrestMethod.RT_READS;
import lib.data.cache.fetcher.FilteredDataFetcher;
import lib.data.cache.fetcher.basecall.Apply2readsBaseCallCountSwitch;
import lib.data.cache.region.RegionDataCache;
import lib.data.count.basecall.BaseCallCount;
import lib.data.filter.BaseCallCountFilteredData;

public class RTarrestReadPositionDistanceFilterFactory 
extends AbstractRTarrestDistanceFilterFactory {

	public RTarrestReadPositionDistanceFilterFactory(
			final Apply2readsBaseCallCountSwitch bccSwitch, 
			final FilteredDataFetcher<BaseCallCountFilteredData, BaseCallCount> filteredDataFetcher) {
		
		super(
				Option.builder(Character.toString('B'))
					.desc("Filter potential false positive variants adjacent to read start/end in read through reads.")
					.build(),
				bccSwitch, filteredDataFetcher,
				6, 0.5);

		getApply2Reads().add(RT_READS.ARREST);
	}
	
	@Override
	protected List<ProcessRecord> createProcessRecord(RegionDataCache regionDataCache) {
		final List<ProcessRecord> processRecords = new ArrayList<ProcessRecord>(1);
		processRecords.add(new ProcessReadStartEnd(getFilterDistance(), regionDataCache));
		return processRecords;
	}
	
}