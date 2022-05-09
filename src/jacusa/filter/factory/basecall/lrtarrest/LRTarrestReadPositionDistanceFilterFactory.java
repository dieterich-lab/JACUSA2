package jacusa.filter.factory.basecall.lrtarrest;

import java.util.List;

import org.apache.commons.cli.Option;

import jacusa.filter.factory.basecall.AbstractBaseCallCountFilterFactory;
import jacusa.filter.factory.basecall.ReadPositionFilterFactory;
import lib.data.DataType;
import lib.data.count.basecall.BaseCallCount;
import lib.data.filter.FilteredBaseCallCount;
import lib.data.storage.PositionProcessor;
import lib.data.storage.container.SharedStorage;
import lib.data.storage.processor.RecordProcessor;

/**
 * This FilterFactory configures and helps to create the filter that will filter
 * false positive variants at the read start and end for lrt-arrest method.
 */
public class LRTarrestReadPositionDistanceFilterFactory extends AbstractBaseCallCountFilterFactory {

	public LRTarrestReadPositionDistanceFilterFactory(final DataType<BaseCallCount> observedDataType,
			final DataType<FilteredBaseCallCount> filteredDataType) {

		super(Option.builder(Character.toString(ReadPositionFilterFactory.FILTER))
				.desc("Filter potential false positive variants adjacent to read start/end in read through reads.")
				.build(), observedDataType, filteredDataType);
	}

	@Override
	protected List<RecordProcessor> createRecordProcessors(final SharedStorage sharedStorage,
			final PositionProcessor positionProcessor) {

		return ReadPositionFilterFactory.createRecordProcessor(sharedStorage, getFilterDistance(), positionProcessor);
	}

}