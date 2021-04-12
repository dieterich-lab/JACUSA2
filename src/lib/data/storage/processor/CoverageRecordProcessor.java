package lib.data.storage.processor;

import lib.data.storage.Storage;
import lib.record.Record;
import lib.util.coordinate.CoordinateTranslator;
import lib.util.position.ConsumingRefPosProviderBuilder;
import lib.util.position.Position;
import lib.util.position.PositionProvider;

/**
 * TODO
 */
public class CoverageRecordProcessor implements GeneralRecordProcessor {

	private final CoordinateTranslator translator;

	private final Storage covStorage;

	public CoverageRecordProcessor(
			final CoordinateTranslator translator,
			final Storage covStorage) {
		
		this.translator	= translator;
		this.covStorage	= covStorage;
	}

	@Override
	public void preProcess() {
		// nothing to be done
	}

	@Override
	public void process(final Record record) {
		// store total coverage
		final PositionProvider covPosProvider = 
				new ConsumingRefPosProviderBuilder(record, translator).build();
		while (covPosProvider.hasNext()) {
			final Position pos = covPosProvider.next();
			covStorage.increment(pos);
		}
	}

	@Override
	public void postProcess() {
		// nothing to be done
	}
	
}
