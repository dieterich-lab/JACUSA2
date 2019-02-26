package jacusa.filter.processrecord;

import java.util.List;

import lib.data.storage.basecall.VisitedReadPositionStorage;
import lib.data.storage.processor.RecordExtendedPrePostProcessor;
import lib.data.storage.processor.RecordExtendedProcessor;
import lib.recordextended.SAMRecordExtended;

/**
 * This class maintains a storage that enables unique processing of each position by positionProcessors.
 */
public class UniquePositionRecordExtendedProcessors implements RecordExtendedPrePostProcessor {

	private final VisitedReadPositionStorage visitedStorage;
	private final List<RecordExtendedProcessor> recordProcessors;

	public UniquePositionRecordExtendedProcessors(
			final VisitedReadPositionStorage visitedStorage,
			final List<RecordExtendedProcessor> processors) {
		
		this.visitedStorage 	= visitedStorage;
		this.recordProcessors 	= processors;
	}

	@Override
	public void preProcess() {
		// nothing to be done
	}
	
	@Override
	public void process(final SAMRecordExtended recordExtended) {
		visitedStorage.reset(recordExtended);
		for (final RecordExtendedProcessor positionProcessor : recordProcessors) {
			positionProcessor.process(recordExtended);
		}
	}

	@Override
	public void postProcess() {
		// nothing to be done
	}
	
}