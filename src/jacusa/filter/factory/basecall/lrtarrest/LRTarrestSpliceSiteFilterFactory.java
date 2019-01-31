package jacusa.filter.factory.basecall.lrtarrest;

import java.util.List;

import jacusa.filter.factory.basecall.SpliceSiteFilterFactory;
import lib.data.fetcher.FilteredDataFetcher;
import lib.data.fetcher.basecall.Apply2readsBaseCallCountSwitch;
import lib.data.filter.ArrestPos2BaseCallCountFilteredData;
import lib.data.storage.PositionProcessor;
import lib.data.storage.container.SharedStorage;
import lib.data.storage.lrtarrest.ArrestPosition2baseCallCount;
import lib.data.storage.processor.RecordExtendedProcessor;

/**
 * TODO add comments.
 */

public class LRTarrestSpliceSiteFilterFactory
extends AbstractLRTarrestBaseCallCountFilterFactory {

	public LRTarrestSpliceSiteFilterFactory(
			final Apply2readsBaseCallCountSwitch bccSwitch, 
			final FilteredDataFetcher<ArrestPos2BaseCallCountFilteredData, ArrestPosition2baseCallCount> filteredDataFetcher) {

		super(
				SpliceSiteFilterFactory.getOptionBuilder().build(),
				bccSwitch, filteredDataFetcher);
	}
	
	@Override
	protected List<RecordExtendedProcessor> createRecordProcessors(
			final SharedStorage sharedStorage, PositionProcessor positionProcessor) {
		
		return SpliceSiteFilterFactory.createRecordProcessors(
				sharedStorage,
				getFilterDistance(), positionProcessor);
	}

}