package jacusa.filter.factory.basecall.rtarrest;

import java.util.List;

import jacusa.filter.factory.basecall.INDELfilterFactory;
import jacusa.method.rtarrest.RTarrestMethod.RT_READS;
import lib.data.count.basecall.BaseCallCount;
import lib.data.fetcher.FilteredDataFetcher;
import lib.data.fetcher.basecall.Apply2readsBaseCallCountSwitch;
import lib.data.filter.BaseCallCountFilteredData;
import lib.data.storage.PositionProcessor;
import lib.data.storage.container.SharedStorage;
import lib.data.storage.processor.RecordProcessor;

/**
 * This FilterFactory configures and helps to create the filter that will filter sites around INDELs
 * for rt-arrest method.
 */
public class RTarrestINDEL_FilterFactory 
extends AbstractRTarrestBaseCallcountFilterFactory {

	public RTarrestINDEL_FilterFactory(
			final Apply2readsBaseCallCountSwitch bccSwitch, 
			final FilteredDataFetcher<BaseCallCountFilteredData, BaseCallCount> filteredDataFetcher) {
		
		super(
				INDELfilterFactory.getOptionBuilder().build(),
				bccSwitch, filteredDataFetcher);
		
		getApply2Reads().add(RT_READS.ARREST);
		getApply2Reads().add(RT_READS.THROUGH);
	}
	

	@Override
	protected List<RecordProcessor> createRecordProcessors(
			SharedStorage sharedStorage, PositionProcessor positionProcessor) {
		
		return INDELfilterFactory.createRecordProcessor(sharedStorage, getFilterDistance(), positionProcessor);
	}

}