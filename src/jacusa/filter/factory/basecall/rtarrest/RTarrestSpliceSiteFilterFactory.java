package jacusa.filter.factory.basecall.rtarrest;

import java.util.List;

import jacusa.filter.factory.basecall.SpliceSiteFilterFactory;
import lib.data.count.basecall.BaseCallCount;
import lib.data.fetcher.FilteredDataFetcher;
import lib.data.fetcher.basecall.Apply2readsBaseCallCountSwitch;
import lib.data.filter.BaseCallCountFilteredData;
import lib.data.storage.PositionProcessor;
import lib.data.storage.container.SharedStorage;
import lib.data.storage.processor.RecordExtendedProcessor;

/**
 * TODO add comments.
 */

public class RTarrestSpliceSiteFilterFactory
extends AbstractRTarrestBaseCallcountFilterFactory {

	public RTarrestSpliceSiteFilterFactory(
			final Apply2readsBaseCallCountSwitch bccSwitch, 
			final FilteredDataFetcher<BaseCallCountFilteredData, BaseCallCount> filteredDataFetcher) {

		super(
				SpliceSiteFilterFactory.getOptionBuilder().build(),
				bccSwitch, filteredDataFetcher);
	}
	
	@Override
	protected List<RecordExtendedProcessor> createRecordProcessors(
			SharedStorage sharedStorage, PositionProcessor positionProcessor) {
		
		return SpliceSiteFilterFactory.createRecordProcessors(
				sharedStorage, getFilterDistance(), positionProcessor);
	}

}