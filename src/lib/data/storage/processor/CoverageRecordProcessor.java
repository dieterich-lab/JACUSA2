package lib.data.storage.processor;

import lib.data.storage.Storage;
import lib.record.Record;
import lib.util.coordinate.CoordinateTranslator;
import lib.util.position.ConsumingRefPosProviderBuilder;
import lib.util.position.Position;
import lib.util.position.PositionProvider;

/**
 * TODO add documentation
 */
public class CoverageRecordProcessor implements ExtendedRecordProcessor {

	private final CoordinateTranslator translator;

	private final Storage coverageStorage;

	public CoverageRecordProcessor(
			final CoordinateTranslator translator,
			final Storage covStorage) {
		
		this.translator			= translator;
		this.coverageStorage	= covStorage;
	}

	@Override
	public void preProcess() {
		// nothing to be done
	}

	@Override
	public void process(final Record record) {
		// store total coverage
		final PositionProvider coveragePositionProvider = 
				new ConsumingRefPosProviderBuilder(record, translator).build();
		while (coveragePositionProvider.hasNext()) {
			final Position pos = coveragePositionProvider.next();
			coverageStorage.increment(pos);
		}
	}

	@Override
	public void postProcess() {
		// nothing to be done
	}
	
}
