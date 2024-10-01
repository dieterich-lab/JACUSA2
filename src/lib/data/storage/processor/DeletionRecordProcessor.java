package lib.data.storage.processor;

import lib.data.storage.Storage;
import lib.record.Record;
import lib.util.coordinate.CoordinateTranslator;
import lib.util.position.AllDeletionsPositionProvider;
import lib.util.position.Position;
import lib.util.position.PositionProvider;

/**
 * TODO add documentation
 */
public class DeletionRecordProcessor implements GeneralRecordProcessor {

	private final CoordinateTranslator translator;
	
	private final Storage delStorage;
	
	public DeletionRecordProcessor(
			final CoordinateTranslator translator,
			final Storage delStorage) {
		
		this.translator	= translator;
		this.delStorage	= delStorage;
	}

	@Override
	public void preProcess() {
		// nothing to be done
	}
	
	@Override // TODO adjust deletion at every position
	public void process(final Record record) {
		// store deletions
		final PositionProvider delPosProvider = 
				new AllDeletionsPositionProvider(record, translator);
		while (delPosProvider.hasNext()) {
			final Position pos = delPosProvider.next();
			delStorage.increment(pos);
		}
	}
	
	@Override
	public void postProcess() {
		// nothing to be done
	}
	
}
