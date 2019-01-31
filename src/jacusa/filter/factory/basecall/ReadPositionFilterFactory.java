package jacusa.filter.factory.basecall;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.Option;

import jacusa.filter.processrecord.ProcessReadStartEnd;
import lib.data.count.basecall.BaseCallCount;
import lib.data.fetcher.Fetcher;
import lib.data.fetcher.FilteredDataFetcher;
import lib.data.filter.BaseCallCountFilteredData;
import lib.data.storage.PositionProcessor;
import lib.data.storage.container.SharedStorage;
import lib.data.storage.processor.RecordExtendedProcessor;

public class ReadPositionFilterFactory 
extends AbstractBaseCallCountFilterFactory {

	public ReadPositionFilterFactory(
			final Fetcher<BaseCallCount> observedBccFetcher,
			final FilteredDataFetcher<BaseCallCountFilteredData, BaseCallCount> filteredDataFetcher) {

		super(
				Option.builder(Character.toString('B'))
					.desc("Filter potential false positive variants adjacent to read start/end.")
					.build(), 
				observedBccFetcher, filteredDataFetcher);
	}

	@Override
	protected List<RecordExtendedProcessor> createRecordProcessors(SharedStorage sharedStorage, final PositionProcessor positionProcessor) {
		return createRecordProcessor(sharedStorage, getFilterDistance(), positionProcessor);
	}
	
	public static List<RecordExtendedProcessor> createRecordProcessor(
			SharedStorage sharedStorage,
			final int filterDistance, final PositionProcessor positionProcessor) {
		
		return Arrays.asList(new ProcessReadStartEnd(
				sharedStorage, filterDistance, positionProcessor) );
	}
	
}