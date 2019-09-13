package jacusa.filter.factory.basecall.rtarrest;

import java.util.List;

import jacusa.filter.factory.basecall.SpliceSiteFilterFactory;
import jacusa.method.rtarrest.RTarrestMethod.RT_READS;
import lib.data.count.basecall.BaseCallCount;
import lib.data.fetcher.FilteredDataFetcher;
import lib.data.fetcher.basecall.Apply2readsBaseCallCountSwitch;
import lib.data.filter.BaseCallCountFilteredData;
import lib.data.storage.PositionProcessor;
import lib.data.storage.container.SharedStorage;
import lib.data.storage.processor.RecordProcessor;

/**
 * This FilterFactory configures and helps to create the filter that filters sites adjacent to 
 * splices sites for rt-arrest method.
 */
public class RTarrestSpliceSiteFilterFactory
extends AbstractRTarrestBaseCallcountFilterFactory {

	public RTarrestSpliceSiteFilterFactory(
			final Apply2readsBaseCallCountSwitch bccSwitch, 
			final FilteredDataFetcher<BaseCallCountFilteredData, BaseCallCount> filteredDataFetcher) {

		super(
				SpliceSiteFilterFactory.getOptionBuilder().build(),
				bccSwitch, filteredDataFetcher);
		
		getApply2Reads().add(RT_READS.ARREST);
		getApply2Reads().add(RT_READS.THROUGH);
	}
	
	@Override
	protected List<RecordProcessor> createRecordProcessors(
			SharedStorage sharedStorage, PositionProcessor positionProcessor) {
		
		return SpliceSiteFilterFactory.createRecordProcessors(
				sharedStorage, getFilterDistance(), positionProcessor);
	}

}