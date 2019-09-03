package jacusa.filter.factory.basecall.lrtarrest;

import java.util.List;

import org.apache.commons.cli.Option;

import jacusa.filter.factory.basecall.ReadPositionFilterFactory;
import jacusa.method.rtarrest.RTarrestMethod.RT_READS;
import lib.data.count.basecall.BaseCallCount;
import lib.data.fetcher.FilteredDataFetcher;
import lib.data.fetcher.basecall.Apply2readsBaseCallCountSwitch;
import lib.data.filter.BaseCallCountFilteredData;
import lib.data.storage.PositionProcessor;
import lib.data.storage.container.SharedStorage;
import lib.data.storage.processor.RecordProcessor;

/**
 * This FilterFactory configures and helps to create the filter that will filter 
 * false positive variants at the read start and end for lrt-arrest method.
 */
public class LRTarrestReadPositionDistanceFilterFactory 
extends AbstractLRTarrestBaseCallCountFilterFactory {

	public LRTarrestReadPositionDistanceFilterFactory(
			final Apply2readsBaseCallCountSwitch bccSwitch, 
			final FilteredDataFetcher<BaseCallCountFilteredData, BaseCallCount> filteredDataFetcher) {
		
		super(
				Option.builder(Character.toString(ReadPositionFilterFactory.FILTER))
					.desc("Filter potential false positive variants adjacent to read start/end in read through reads.")
					.build(),
				bccSwitch, filteredDataFetcher);

		getApply2Reads().add(RT_READS.ARREST);
	}
	
	@Override
	protected List<RecordProcessor> createRecordProcessors(
			final SharedStorage sharedStorage, final PositionProcessor positionProcessor) {
		
		return ReadPositionFilterFactory.createRecordProcessor(
				sharedStorage, getFilterDistance(), positionProcessor);
	}
	
}