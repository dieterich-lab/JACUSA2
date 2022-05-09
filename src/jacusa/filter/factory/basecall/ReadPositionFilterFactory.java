package jacusa.filter.factory.basecall;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.Option;

import jacusa.filter.processrecord.ProcessReadStartEnd;
import lib.data.DataType;
import lib.data.count.basecall.BaseCallCount;
import lib.data.filter.FilteredBaseCallCount;
import lib.data.storage.PositionProcessor;
import lib.data.storage.container.SharedStorage;
import lib.data.storage.processor.RecordProcessor;

/**
 * This FilterFactory configures and helps to create the filter that will filter
 * false positive variants at the read start and end.
 */
public class ReadPositionFilterFactory extends AbstractBaseCallCountFilterFactory {

	public static final char FILTER = 'B';

	public ReadPositionFilterFactory(final DataType<BaseCallCount> observedDataType,
			final DataType<FilteredBaseCallCount> filteredDataType) {

		super(Option.builder(Character.toString(FILTER))
				.desc("Filter potential false positive variants adjacent to read start/end.").build(), observedDataType,
				filteredDataType);
	}

	@Override
	protected List<RecordProcessor> createRecordProcessors(SharedStorage sharedStorage,
			final PositionProcessor positionProcessor) {

		return createRecordProcessor(sharedStorage, getFilterDistance(), positionProcessor);
	}

	public static List<RecordProcessor> createRecordProcessor(SharedStorage sharedStorage, final int filterDistance,
			final PositionProcessor positionProcessor) {

		return Arrays.asList(new ProcessReadStartEnd(sharedStorage, filterDistance, positionProcessor));
	}

}