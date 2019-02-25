package jacusa.filter.processrecord;

import lib.data.storage.PositionProcessor;
import lib.data.storage.container.SharedStorage;
import lib.data.storage.processor.RecordExtendedProcessor;
import lib.util.coordinate.CoordinateTranslator;

/**
 * This abstract class enables to process reads with a chosen positionProcessor.
 */
abstract class AbstractFilterRecordExtendedProcessor implements RecordExtendedProcessor {

	private final SharedStorage sharedStorage;
	
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
	 * Returns the positionProcessor that is used to traverse the read.
	 * 
	 * @return
	 */
	PositionProcessor getPositionProcessor() {
		return positionProcessor;
	}
	
}
