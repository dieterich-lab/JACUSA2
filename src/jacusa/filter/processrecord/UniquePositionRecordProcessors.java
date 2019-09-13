package jacusa.filter.processrecord;

import java.util.List;

import lib.data.storage.basecall.VisitedReadPositionStorage;
import lib.data.storage.processor.GeneralRecordProcessor;
import lib.data.storage.processor.RecordProcessor;
import lib.record.Record;

/**
 * This class maintains a storage that enables unique processing of each position by positionProcessors.
 */
public class UniquePositionRecordProcessors implements GeneralRecordProcessor {

	private final VisitedReadPositionStorage visitedStorage;
	private final List<RecordProcessor> recordProcessors;

	public UniquePositionRecordProcessors(
			final VisitedReadPositionStorage visitedStorage,
			final List<RecordProcessor> processors) {
		
		this.visitedStorage 	= visitedStorage;
		this.recordProcessors 	= processors;
	}

	@Override
	public void preProcess() {
		// nothing to be done
	}
	
	@Override
	public void process(final Record record) {
		visitedStorage.reset(record);
		for (final RecordProcessor positionProcessor : recordProcessors) {
			positionProcessor.process(record);
		}
	}

	@Override
	public void postProcess() {
		// nothing to be done
	}
	
}
