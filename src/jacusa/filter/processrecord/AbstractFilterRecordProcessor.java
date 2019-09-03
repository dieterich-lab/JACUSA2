package jacusa.filter.processrecord;

import lib.data.storage.PositionProcessor;
import lib.data.storage.container.SharedStorage;
import lib.data.storage.processor.RecordProcessor;
import lib.util.coordinate.CoordinateTranslator;

/**
 * This abstract class enables to process reads with a chosen positionProcessor.
 */
abstract class AbstractFilterRecordProcessor implements RecordProcessor {
	
	private final SharedStorage sharedStorage;
	
	private final int distance;
	// here, ensures that each position is only counted once
	private final PositionProcessor positionProcessor;
	
	public AbstractFilterRecordProcessor(
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
	 * Distance up or downstream.
	 * 
	 * @return distance up or downstream.
	 */
	int getDistance() {
		return distance;
	}
	
	/**
	 * Returns the positionProcessor that is used to traverse the read.
	 * 
	 * @return the positionProcessor that is used to traverse the read
	 */
	PositionProcessor getPositionProcessor() {
		return positionProcessor;
	}
	
}
