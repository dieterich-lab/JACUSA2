package lib.data.storage.processor;

import lib.recordextended.SAMRecordExtended;

import lib.data.storage.PositionProcessor;
import lib.util.coordinate.CoordinateTranslator;
import lib.util.position.PositionProvider;
import lib.util.position.ContinousAligmnentPositionProvider;

public class AlignmentBlockProcessor implements RecordExtendedPrePostProcessor {

	private final CoordinateTranslator translator;
	private final PositionProcessor positionProcessor;
	
	public AlignmentBlockProcessor(
			final CoordinateTranslator translator, final PositionProcessor positionProcessor) {
		
		this.translator 		= translator;
		this.positionProcessor 	= positionProcessor;
	}

	@Override
	public void preProcess() {
		// nothing to be done
	}
	
	@Override
	public void process(final SAMRecordExtended recordExtended) {
		final PositionProvider positionProvider = new ContinousAligmnentPositionProvider(
						recordExtended, translator);
		positionProcessor.process(positionProvider);
	}
	
	@Override
	public void postProcess() {
		// nothing to be done
	}
	
}
