package jacusa.filter.factory.basecall.rtarrest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Option.Builder;

import htsjdk.samtools.util.StringUtil;
import jacusa.filter.factory.basecall.CombinedFilterFactory;
import jacusa.filter.factory.basecall.INDELfilterFactory;
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
 * This FilterFactory configures and helps to create the combined filter which aggregates the counts 
 * of other more basic filters for rt-arrest method.
 */
public class RTarrestCombinedFilterFactory 
extends AbstractRTarrestBaseCallcountFilterFactory {

	public RTarrestCombinedFilterFactory(
			final Apply2readsBaseCallCountSwitch bccSwitch, 
			final FilteredDataFetcher<BaseCallCountFilteredData, BaseCallCount> filteredDataFetcher) {

		super(
				getOptionBuilder().build(),
				bccSwitch, filteredDataFetcher);

		getApply2Reads().add(RT_READS.ARREST);
		getApply2Reads().add(RT_READS.THROUGH);
	}

	@Override
	protected List<RecordProcessor> createRecordProcessors(
			SharedStorage sharedStorage, PositionProcessor positionProcessor) {
		
		return createRecordProcessors(sharedStorage, getFilterDistance(), positionProcessor);
	}
	
	public static List<RecordProcessor> createRecordProcessors(
			final SharedStorage sharedStorage,
			final int filterDistance, 
			PositionProcessor positionProcessor) {
		
		final List<RecordProcessor> processRecords = new ArrayList<>();
		// INDELs
		processRecords.addAll(INDELfilterFactory.createRecordProcessor(
				sharedStorage, filterDistance, positionProcessor));
		// introns
		processRecords.addAll(SpliceSiteFilterFactory.createRecordProcessors(
				sharedStorage, filterDistance, positionProcessor));
		return processRecords;
	}

	public static Builder getOptionBuilder() {
		return Option.builder(Character.toString(CombinedFilterFactory.FILTER))
				.desc("Combines Filters: " + StringUtil.join(" + ", Arrays.asList(INDELfilterFactory.FILTER, SpliceSiteFilterFactory.FILTER)));
	}
	
}
