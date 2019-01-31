package lib.data.storage.arrest;

import lib.recordextended.SAMRecordExtended;
import lib.data.storage.PositionProcessor;
import lib.data.storage.container.SharedStorage;
import lib.data.storage.processor.RecordExtendedPrePostProcessor;
import lib.util.coordinate.CoordinateTranslator;

public class RTarrestRecordProcessor 
implements RecordExtendedPrePostProcessor {

	private final SharedStorage shareStorage;
	
	private final LocationInterpreter locInterpreter;
	
	private final PositionProcessor arrestPositionProcessor;
	private final PositionProcessor throughPositionProcessor;

	public RTarrestRecordProcessor(
			final SharedStorage sharedStorage,
			final LocationInterpreter locactionInterpreter,
			final PositionProcessor arrestPositionProcessor,
			final PositionProcessor throughPositionProcessor) {
		
		this.shareStorage				= sharedStorage;
		
		locInterpreter 					= locactionInterpreter;
		
		this.arrestPositionProcessor 	= arrestPositionProcessor;
		this.throughPositionProcessor 	= throughPositionProcessor;
	}

	private CoordinateTranslator getTranslator() {
		return shareStorage.getCoordinateController().getCoordinateTranslator();
	}
	
	@Override
	public void preProcess() {
		// nothing to be done
	}
	
	@Override
	public void process(SAMRecordExtended recordExtended) {
		arrestPositionProcessor.process(
				locInterpreter.getArrestPositionProvider(recordExtended, getTranslator()));
		
		throughPositionProcessor.process(
				locInterpreter.getThroughPositionProvider(recordExtended, getTranslator()));
	}

	@Override
	public void postProcess() {
		// nothing to be done
	}
	
}
