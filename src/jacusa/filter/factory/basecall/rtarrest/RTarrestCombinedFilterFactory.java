package jacusa.filter.factory.basecall.rtarrest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Option.Builder;

import htsjdk.samtools.util.StringUtil;
import jacusa.filter.factory.basecall.AbstractBaseCallCountFilterFactory;
import jacusa.filter.factory.basecall.CombinedFilterFactory;
import jacusa.filter.factory.basecall.INDELfilterFactory;
import jacusa.filter.factory.basecall.SpliceSiteFilterFactory;
import lib.data.DataType;
import lib.data.count.basecall.BaseCallCount;
import lib.data.filter.FilteredBaseCallCount;
import lib.data.storage.PositionProcessor;
import lib.data.storage.container.SharedStorage;
import lib.data.storage.processor.RecordProcessor;

/**
 * This FilterFactory configures and helps to create the combined filter which
 * aggregates the counts of other more basic filters for rt-arrest method.
 */
public class RTarrestCombinedFilterFactory extends AbstractBaseCallCountFilterFactory {

	public RTarrestCombinedFilterFactory(final DataType<BaseCallCount> observedDataType,
			final DataType<FilteredBaseCallCount> filteredDataType) {

		super(getOptionBuilder().build(), observedDataType, filteredDataType);
	}

	@Override
	protected List<RecordProcessor> createRecordProcessors(SharedStorage sharedStorage,
			PositionProcessor positionProcessor) {

		return createRecordProcessors(sharedStorage, getFilterDistance(), positionProcessor);
	}

	public static List<RecordProcessor> createRecordProcessors(final SharedStorage sharedStorage,
			final int filterDistance, PositionProcessor positionProcessor) {

		final List<RecordProcessor> processRecords = new ArrayList<>();
		// INDELs
		processRecords
				.addAll(INDELfilterFactory.createRecordProcessor(sharedStorage, filterDistance, positionProcessor));
		// introns
		processRecords.addAll(
				SpliceSiteFilterFactory.createRecordProcessors(sharedStorage, filterDistance, positionProcessor));
		return processRecords;
	}

	public static Builder getOptionBuilder() {
		return Option.builder(Character.toString(CombinedFilterFactory.FILTER)).desc("Combines Filters: "
				+ StringUtil.join(" + ", Arrays.asList(INDELfilterFactory.FILTER, SpliceSiteFilterFactory.FILTER)));
	}

}
