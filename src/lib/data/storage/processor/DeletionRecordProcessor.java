package lib.data.storage.processor;

import lib.data.storage.Storage;
import lib.record.Record;
import lib.util.coordinate.CoordinateTranslator;
import lib.util.position.AllDeletionPositionProvider;
import lib.util.position.Position;
import lib.util.position.PositionProvider;

/**
 * DOCUMENT
 */
public class DeletionRecordProcessor implements ExtendedRecordProcessor {

	private final CoordinateTranslator translator;
	private final Storage delStorage;
	
	public DeletionRecordProcessor(
			final CoordinateTranslator coordinateTranslator,
			final Storage delStorage) {
		
		this.translator	= coordinateTranslator;
		this.delStorage	= delStorage;
	}

	@Override
	public void preProcess() {
		// nothing to be done
	}
	
	@Override // TODO adjust deletion at every position
	public void process(final Record record) {
		// store deletions
		final PositionProvider delPositionProvider = new AllDeletionPositionProvider(record, translator);
		while (delPositionProvider.hasNext()) {
			final Position pos = delPositionProvider.next();
			delStorage.increment(pos);
		}
	}
	
	@Override
	public void postProcess() {
		// nothing to be done
	}
	
}
