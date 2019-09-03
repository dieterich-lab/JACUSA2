package jacusa.filter.factory.basecall.lrtarrest;

import java.util.List;

import jacusa.filter.factory.basecall.SpliceSiteFilterFactory;
import lib.data.count.basecall.BaseCallCount;
import lib.data.fetcher.FilteredDataFetcher;
import lib.data.fetcher.basecall.Apply2readsBaseCallCountSwitch;
import lib.data.filter.BaseCallCountFilteredData;
import lib.data.storage.PositionProcessor;
import lib.data.storage.container.SharedStorage;
import lib.data.storage.processor.RecordProcessor;

/**
 * This FilterFactory configures and helps to create the filter that filters sites adjacent to 
 * splices sites for lrt-arrest method.
 */
public class LRTarrestSpliceSiteFilterFactory
extends AbstractLRTarrestBaseCallCountFilterFactory {

	public LRTarrestSpliceSiteFilterFactory(
			final Apply2readsBaseCallCountSwitch bccSwitch, 
			final FilteredDataFetcher<BaseCallCountFilteredData, BaseCallCount> filteredDataFetcher) {
		
		super(
				SpliceSiteFilterFactory.getOptionBuilder().build(),
				bccSwitch, filteredDataFetcher);
	}
	
	@Override
	protected List<RecordProcessor> createRecordProcessors(
			final SharedStorage sharedStorage, PositionProcessor positionProcessor) {
		
		return SpliceSiteFilterFactory.createRecordProcessors(
				sharedStorage,
				getFilterDistance(), positionProcessor);
	}

}