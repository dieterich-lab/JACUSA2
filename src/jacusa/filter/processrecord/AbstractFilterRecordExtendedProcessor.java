package jacusa.filter.processrecord;

import lib.data.storage.PositionProcessor;
import lib.data.storage.container.SharedStorage;
import lib.data.storage.processor.RecordExtendedProcessor;
import lib.util.coordinate.CoordinateTranslator;

/**
 * TODO add comments
 */
abstract class AbstractFilterRecordExtendedProcessor implements RecordExtendedProcessor {

	private final SharedStorage sharedStorage;
	
	// TODO add comments.
	private final int distance;
	// ensures that each position is only counted once
	private final PositionProcessor positionProcessor;
	
	public AbstractFilterRecordExtendedProcessor(
			final SharedStorage sharedStorage,
			final int distance, 
			final PositionProcessor positionProcessor) {

		this.sharedStorage		= sharedStorage;
		this.distance 			= distance;
		this.positionProcessor 	= positionProcessor;
	}

	CoordinateTranslator getTranslator() {
		return sharedStorage.getCoordinateController().getCoordinateTranslator();
	}
	
	/**
	 * TODO add comments.
	 * 
	 * @return
	 */
	int getDistance() {
		return distance;
	}
	
	/**
	 * TODO add comments.
	 * 
	 * @return
	 */
	PositionProcessor getPositionProcessor() {
		return positionProcessor;
	}
	
}
