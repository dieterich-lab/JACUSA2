package jacusa.filter.factory.basecall;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Option.Builder;

import jacusa.filter.processrecord.ProcessDeletionOperator;
import jacusa.filter.processrecord.ProcessInsertionOperator;
import lib.data.count.basecall.BaseCallCount;
import lib.data.fetcher.Fetcher;
import lib.data.fetcher.FilteredDataFetcher;
import lib.data.filter.BaseCallCountFilteredData;
import lib.data.storage.PositionProcessor;
import lib.data.storage.container.SharedStorage;
import lib.data.storage.processor.RecordExtendedProcessor;

/**
 * TODO add comments.
 * 
 * @param 
 */

public class INDEL_FilterFactory extends AbstractBaseCallCountFilterFactory {

	public static final char FILTER = 'I';
	
	public INDEL_FilterFactory(
			final Fetcher<BaseCallCount> observedBccFetcher,
			FilteredDataFetcher<BaseCallCountFilteredData, BaseCallCount> filteredDataFetcher) {
		super(
				getOptionBuilder().build(),
				observedBccFetcher, filteredDataFetcher);
	}

	@Override
	protected List<RecordExtendedProcessor> createRecordProcessors(SharedStorage sharedStorage, PositionProcessor positionProcessor) {
		return createRecordProcessor(sharedStorage, getFilterDistance(), positionProcessor);
	}

	public static List<RecordExtendedProcessor> createRecordProcessor(
			SharedStorage sharedStorage, 
			final int filterDistance, PositionProcessor positionProcessor) {
		
		return Arrays.asList(
				new ProcessInsertionOperator(sharedStorage, filterDistance, positionProcessor),
				new ProcessDeletionOperator(sharedStorage, filterDistance, positionProcessor) );
	}
	
	public static Builder getOptionBuilder() {
		return Option.builder(Character.toString(FILTER))
				.desc("Filter potential false positive variants adjacent to INDEL position(s).");
	}
	
}