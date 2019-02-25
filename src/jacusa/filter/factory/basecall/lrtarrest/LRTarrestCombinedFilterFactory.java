package jacusa.filter.factory.basecall.lrtarrest;

import java.util.List;

import jacusa.filter.factory.basecall.rtarrest.RTarrestCombinedFilterFactory;
import jacusa.method.rtarrest.RTarrestMethod.RT_READS;
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

public class LRTarrestCombinedFilterFactory 
extends AbstractLRTarrestBaseCallCountFilterFactory {

	public LRTarrestCombinedFilterFactory(
			final Apply2readsBaseCallCountSwitch bccSwitch, 
			final FilteredDataFetcher<BaseCallCountFilteredData, BaseCallCount> filteredDataFetcher) {
		
		super(
				RTarrestCombinedFilterFactory.getOptionBuilder().build(),
				bccSwitch, filteredDataFetcher);
		getApply2Reads().add(RT_READS.ARREST);
	}

	@Override
	protected List<RecordExtendedProcessor> createRecordProcessors(
			final SharedStorage sharedStorage, PositionProcessor positionProcessor) {
		
		return RTarrestCombinedFilterFactory.createRecordProcessors(
				sharedStorage,
				getFilterDistance(), positionProcessor);
	}

}
