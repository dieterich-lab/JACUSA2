package lib.data.storage.processor;

import lib.data.storage.PositionProcessor;
import lib.record.ProcessedRecord;
import lib.util.coordinate.CoordinateTranslator;
import lib.util.position.AllAlignmentBlocksPosProvider;
import lib.util.position.PositionProvider;

/**
 * TODO
 */
public class AlignmentBlockProcessor implements GeneralRecordProcessor {

	private final CoordinateTranslator translator;
	private final PositionProcessor positionProcessor;

	public AlignmentBlockProcessor(final CoordinateTranslator translator, final PositionProcessor positionProcessor) {

		this.translator = translator;
		this.positionProcessor = positionProcessor;
	}

	@Override
	public void preProcess() {
		// nothing to be done
	}

	@Override
	public void process(final ProcessedRecord record) {
		final PositionProvider positionProvider = new AllAlignmentBlocksPosProvider(record, translator);
		positionProcessor.process(positionProvider);
	}

	@Override
	public void postProcess() {
		// nothing to be done
	}

}
