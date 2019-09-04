package jacusa.filter.factory.basecall;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Option.Builder;

import jacusa.filter.processrecord.ProcessSkippedOperator;
import lib.data.count.basecall.BaseCallCount;
import lib.data.fetcher.Fetcher;
import lib.data.fetcher.FilteredDataFetcher;
import lib.data.filter.BaseCallCountFilteredData;
import lib.data.storage.PositionProcessor;
import lib.data.storage.container.SharedStorage;
import lib.data.storage.processor.RecordProcessor;

/**
 * This FilterFactory configures and helps to create the filter that filters 
 * sites adjacent to splices sites.
 */
public class SpliceSiteFilterFactory
extends AbstractBCCfilterFactory {

	public static final char FILTER = 'S';
	
	public SpliceSiteFilterFactory(
			final Fetcher<BaseCallCount> observedBccFetcher,
			final FilteredDataFetcher<BaseCallCountFilteredData, BaseCallCount> filteredDataFetcher) {
		
		super(
				getOptionBuilder().build(),
				observedBccFetcher, filteredDataFetcher);
	}
	
	@Override
	protected List<RecordProcessor> createRecordProcessors(
			SharedStorage sharedStorage, final PositionProcessor positionProcessor) {
		
		return createRecordProcessors(sharedStorage, getFilterDistance(), positionProcessor);
	}
	
	public static List<RecordProcessor> createRecordProcessors(
			final SharedStorage sharedStorage,
			final int filterDistance, 
			final PositionProcessor positionProcessor) {
		
		return Arrays.asList(new ProcessSkippedOperator(
				sharedStorage, filterDistance, positionProcessor));
	}
	
	public static Builder getOptionBuilder() {
		return Option.builder(Character.toString(FILTER))
				.desc("Filter potential false positive variants adjacent to splice site(s).");
	}
	
}