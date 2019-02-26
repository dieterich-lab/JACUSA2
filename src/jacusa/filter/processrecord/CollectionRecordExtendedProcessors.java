package jacusa.filter.processrecord;

import java.util.List;

import lib.data.storage.processor.RecordExtendedPrePostProcessor;
import lib.data.storage.processor.RecordExtendedProcessor;
import lib.recordextended.SAMRecordExtended;

/**
 * This class maintains a storage that enables unique processing of each position by positionProcessors.
 */
public class CollectionRecordExtendedProcessors implements RecordExtendedPrePostProcessor {

	private final List<RecordExtendedProcessor> recordProcessors;

	public CollectionRecordExtendedProcessors(final List<RecordExtendedProcessor> recordProcessors) {
		this.recordProcessors = recordProcessors;
	}

	@Override
	public void preProcess() {
		// nothing to be done
	}
	
	@Override
	public void process(final SAMRecordExtended recordExtended) {
		for (final RecordExtendedProcessor positionProcessor : recordProcessors) {
			positionProcessor.process(recordExtended);
		}
	}

	@Override
	public void postProcess() {
		// nothing to be done
	}
	
}