package lib.data.storage.arrest;

import lib.data.storage.PositionProcessor;
import lib.data.storage.container.SharedStorage;
import lib.data.storage.processor.GeneralRecordProcessor;
import lib.record.ProcessedRecord;
import lib.util.coordinate.CoordinateTranslator;
import lib.util.position.Position;

public class RTarrestRecordProcessor 
implements GeneralRecordProcessor {

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
	public void process(ProcessedRecord record) {
		if (locInterpreter.hasArrestPosition(record)) {
			final Position arrestPos = locInterpreter.getArrestPosition(record, getTranslator());
			if (arrestPos != null && arrestPositionProcessor.checkValidators(arrestPos)) {
				arrestPositionProcessor.processStorages(arrestPos);
			}
		}

		throughPositionProcessor.process(
				locInterpreter.getThroughPositionProvider(record, getTranslator()));
	}

	@Override
	public void postProcess() {
		// nothing to be done
	}
	
}
