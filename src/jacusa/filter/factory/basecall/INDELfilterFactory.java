package jacusa.filter.factory.basecall;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Option.Builder;

import jacusa.filter.processrecord.ProcessDeletionOperator;
import jacusa.filter.processrecord.ProcessInsertionOperator;
import lib.data.DataType;
import lib.data.count.basecall.BaseCallCount;
import lib.data.filter.FilteredBaseCallCount;
import lib.data.storage.PositionProcessor;
import lib.data.storage.container.SharedStorage;
import lib.data.storage.processor.RecordProcessor;

/**
 * This FilterFactory configures and helps to create the filter that will filter
 * sites around INDELs.
 */
public class INDELfilterFactory extends AbstractBaseCallCountFilterFactory {

	public static final char FILTER = 'I';

	public INDELfilterFactory(final DataType<BaseCallCount> observedDataType,
			final DataType<FilteredBaseCallCount> filteredDataType) {
		super(getOptionBuilder().build(), observedDataType, filteredDataType);
	}

	@Override
	protected List<RecordProcessor> createRecordProcessors(SharedStorage sharedStorage,
			PositionProcessor positionProcessor) {
		return createRecordProcessor(sharedStorage, getFilterDistance(), positionProcessor);
	}

	public static List<RecordProcessor> createRecordProcessor(SharedStorage sharedStorage, final int filterDistance,
			PositionProcessor positionProcessor) {

		return Arrays.asList(new ProcessInsertionOperator(sharedStorage, filterDistance, positionProcessor),
				new ProcessDeletionOperator(sharedStorage, filterDistance, positionProcessor));
	}

	public static Builder getOptionBuilder() {
		final String s = "Filter potential false positive variants adjacent " + "to INDEL position(s).";
		return Option.builder(Character.toString(FILTER)).desc(s);
	}

}
