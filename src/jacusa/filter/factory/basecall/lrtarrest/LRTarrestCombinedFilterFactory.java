package jacusa.filter.factory.basecall.lrtarrest;

import java.util.List;

import jacusa.filter.factory.basecall.AbstractBaseCallCountFilterFactory;
import jacusa.filter.factory.basecall.rtarrest.RTarrestCombinedFilterFactory;
import lib.data.DataType;
import lib.data.count.basecall.BaseCallCount;
import lib.data.filter.FilteredBaseCallCount;
import lib.data.storage.PositionProcessor;
import lib.data.storage.container.SharedStorage;
import lib.data.storage.processor.RecordProcessor;

/**
 * This FilterFactory configures and helps to create the combined filter which
 * aggregates the counts of other more basic filters for lrt-arrest method.
 */
public class LRTarrestCombinedFilterFactory extends AbstractBaseCallCountFilterFactory {

	public LRTarrestCombinedFilterFactory(final DataType<BaseCallCount> observedDataType,
			final DataType<FilteredBaseCallCount> filteredDataType) {

		super(RTarrestCombinedFilterFactory.getOptionBuilder().build(), observedDataType, filteredDataType);
	}

	@Override
	protected List<RecordProcessor> createRecordProcessors(final SharedStorage sharedStorage,
			PositionProcessor positionProcessor) {

		return RTarrestCombinedFilterFactory.createRecordProcessors(sharedStorage, getFilterDistance(),
				positionProcessor);
	}

}
