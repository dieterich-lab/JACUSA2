package jacusa.filter.factory.basecall.lrtarrest;

import java.util.List;

import jacusa.filter.factory.basecall.INDELfilterFactory;
import lib.data.count.basecall.BaseCallCount;
import lib.data.fetcher.FilteredDataFetcher;
import lib.data.fetcher.basecall.Apply2readsBaseCallCountSwitch;
import lib.data.filter.BaseCallCountFilteredData;
import lib.data.storage.PositionProcessor;
import lib.data.storage.container.SharedStorage;
import lib.data.storage.processor.RecordProcessor;

/**
 * This FilterFactory configures and helps to create the filter that will filter sites around INDELs
 * for lrt-arrest method.
 */
public class LRTarrestINDELfilterFactory 
extends AbstractLRTarrestBaseCallCountFilterFactory {

	public LRTarrestINDELfilterFactory(
			final Apply2readsBaseCallCountSwitch bccSwitch, 
			final FilteredDataFetcher<BaseCallCountFilteredData, BaseCallCount> filteredDataFetcher) {
		
		super(
				INDELfilterFactory.getOptionBuilder().build(),
				bccSwitch, filteredDataFetcher);
	}

	@Override
	protected List<RecordProcessor> createRecordProcessors(
			final SharedStorage sharedStorage, final PositionProcessor positionProcessor) {
		
		return INDELfilterFactory.createRecordProcessor(
				sharedStorage, getFilterDistance(), positionProcessor);
	}

}