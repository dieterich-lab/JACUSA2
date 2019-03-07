package lib.data.storage.arrest;

import lib.recordextended.SAMRecordExtended;
import lib.data.storage.PositionProcessor;
import lib.data.storage.container.SharedStorage;
import lib.data.storage.processor.RecordExtendedPrePostProcessor;
import lib.util.coordinate.CoordinateTranslator;
import lib.util.position.Position;

public class RTarrestRecordProcessor 
implements RecordExtendedPrePostProcessor {

	private final SharedStorage sharedStorage;
	
	private final LocationInterpreter locInterpreter;
	
	private final PositionProcessor arrestPositionProcessor;
	private final PositionProcessor throughPositionProcessor;

	public RTarrestRecordProcessor(
			final SharedStorage sharedStorage,
			final LocationInterpreter locactionInterpreter,
			final PositionProcessor arrestPositionProcessor,
			final PositionProcessor throughPositionProcessor) {
		
		this.sharedStorage				= sharedStorage;
		
		locInterpreter 					= locactionInterpreter;
		
		this.arrestPositionProcessor 	= arrestPositionProcessor;
		this.throughPositionProcessor 	= throughPositionProcessor;
	}

	private CoordinateTranslator getTranslator() {
		return sharedStorage.getCoordinateController().getCoordinateTranslator();
	}
	
	@Override
	public void preProcess() {
		// nothing to be done
	}
	
	@Override
	public void process(SAMRecordExtended recordExtended) {
		if (locInterpreter.hasArrestPosition(recordExtended)) {
			final Position arrestPos = locInterpreter.getArrestPosition(recordExtended, getTranslator());
			if (arrestPos != null && arrestPositionProcessor.checkValidators(arrestPos)) {
				arrestPositionProcessor.processStorages(arrestPos);
			}
		}

		throughPositionProcessor.process(
				locInterpreter.getThroughPositionProvider(recordExtended, getTranslator()));
	}

	@Override
	public void postProcess() {
		// nothing to be done
	}
	
}
