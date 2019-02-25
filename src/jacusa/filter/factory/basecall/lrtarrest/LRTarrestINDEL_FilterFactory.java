package jacusa.filter.factory.basecall.lrtarrest;

import java.util.List;

import jacusa.filter.factory.basecall.INDEL_FilterFactory;
import lib.data.count.basecall.BaseCallCount;
import lib.data.fetcher.FilteredDataFetcher;
import lib.data.fetcher.basecall.Apply2readsBaseCallCountSwitch;
import lib.data.filter.BaseCallCountFilteredData;
import lib.data.storage.PositionProcessor;
import lib.data.storage.container.SharedStorage;
import lib.data.storage.processor.RecordExtendedProcessor;

/**
 * TODO add comments.
 * 
 * @param 
 */

public class LRTarrestINDEL_FilterFactory 
extends AbstractLRTarrestBaseCallCountFilterFactory {

	public LRTarrestINDEL_FilterFactory(
			final Apply2readsBaseCallCountSwitch bccSwitch, 
			final FilteredDataFetcher<BaseCallCountFilteredData, BaseCallCount> filteredDataFetcher) {
		
		super(
				INDEL_FilterFactory.getOptionBuilder().build(),
				bccSwitch, filteredDataFetcher);
	}

	@Override
	protected List<RecordExtendedProcessor> createRecordProcessors(
			final SharedStorage sharedStorage, final PositionProcessor positionProcessor) {
		
		return INDEL_FilterFactory.createRecordProcessor(
				sharedStorage, getFilterDistance(), positionProcessor);
	}

}