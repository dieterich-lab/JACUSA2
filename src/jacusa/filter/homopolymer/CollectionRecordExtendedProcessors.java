package jacusa.filter.homopolymer;

import java.util.List;

import lib.data.storage.basecall.VisitedReadPositionStorage;
import lib.data.storage.processor.RecordExtendedPrePostProcessor;
import lib.data.storage.processor.RecordExtendedProcessor;
import lib.recordextended.SAMRecordExtended;

/**
 * TODO add comments.
 * 
 * @param 
 */
public class CollectionRecordExtendedProcessors implements RecordExtendedPrePostProcessor {

	private final VisitedReadPositionStorage visitedStorage;
	private final List<RecordExtendedProcessor> processors;

	public CollectionRecordExtendedProcessors(
			final VisitedReadPositionStorage visitedStorage,
			final List<RecordExtendedProcessor> processors) {
		
		this.visitedStorage = visitedStorage;
		this.processors = processors;
	}

	@Override
	public void preProcess() {
		// nothing to be done
	}
	
	@Override
	public void process(final SAMRecordExtended recordExtended) {
		visitedStorage.reset(recordExtended);
		for (final RecordExtendedProcessor p : processors) {
			p.process(recordExtended);
		}
	}

	@Override
	public void postProcess() {
		// nothing to be done
	}
	
	
	
}