package jacusa.filter.factory.basecall;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Option.Builder;

import jacusa.filter.processrecord.ProcessSkippedOperator;
import lib.data.DataType;
import lib.data.count.basecall.BaseCallCount;
import lib.data.filter.FilteredBaseCallCount;
import lib.data.storage.PositionProcessor;
import lib.data.storage.container.SharedStorage;
import lib.data.storage.processor.RecordProcessor;

/**
 * This FilterFactory configures and helps to create the filter that filters 
 * sites adjacent to splices sites.
 */
public class SpliceSiteFilterFactory
extends AbstractBaseCallCountFilterFactory {

	public static final char FILTER = 'S';
	
	public SpliceSiteFilterFactory(
			final DataType<BaseCallCount> observedDataType,
			final DataType<FilteredBaseCallCount> filteredDataType) {
		
		super(
				getOptionBuilder().build(),
				observedDataType, filteredDataType);
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