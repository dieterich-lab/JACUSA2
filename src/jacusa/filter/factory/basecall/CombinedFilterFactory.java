package jacusa.filter.factory.basecall;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Option.Builder;

import htsjdk.samtools.util.StringUtil;
import lib.data.count.basecall.BaseCallCount;
import lib.data.fetcher.Fetcher;
import lib.data.fetcher.FilteredDataFetcher;
import lib.data.filter.BaseCallCountFilteredData;
import lib.data.storage.PositionProcessor;
import lib.data.storage.container.SharedStorage;
import lib.data.storage.processor.RecordProcessor;

/**
 * This FilterFactory configures and helps to create the combined filter which aggregates the counts 
 * of other more basic filters.
 */
public class CombinedFilterFactory extends AbstractBaseCallCountFilterFactory {

	public static final char FILTER = 'D';
	
	public CombinedFilterFactory(
			final Fetcher<BaseCallCount> observedBccFetcher,
			final FilteredDataFetcher<BaseCallCountFilteredData, BaseCallCount> filteredDataFetcher) {
		
		super(
				getOptionBuilder().build(),
				observedBccFetcher, filteredDataFetcher);
	}

	@Override
	protected List<RecordProcessor> createRecordProcessors(
			SharedStorage sharedStorage, PositionProcessor positionProcessor) {
		
		return createRecordProcessors(sharedStorage, getFilterDistance(), positionProcessor);
	}
	
	// make the collection of recordProcessors available to other factories
	public static List<RecordProcessor> createRecordProcessors(
			final SharedStorage sharedStorage,
			final int filterDistance, 
			final PositionProcessor positionProcessor) {
		
		final List<RecordProcessor> processRecords = new ArrayList<>();
		// INDELs
		processRecords.addAll(
				INDELfilterFactory.createRecordProcessor(
						sharedStorage, filterDistance, positionProcessor));
		// read start end 
		processRecords.addAll(
				ReadPositionFilterFactory.createRecordProcessor(
						sharedStorage, filterDistance, positionProcessor));
		// introns
		processRecords.addAll(
				SpliceSiteFilterFactory.createRecordProcessors(
						sharedStorage, filterDistance, positionProcessor));
		
		return processRecords;
	}
	
	public static Builder getOptionBuilder() {
		final List<Character> factories = Arrays.asList(
				INDELfilterFactory.FILTER, 
				ReadPositionFilterFactory.FILTER, 
				SpliceSiteFilterFactory.FILTER);
		
		return Option.builder(Character.toString(FILTER))
				.desc("Combines Filters: " + StringUtil.join(" + ", factories));
	}
	
}
