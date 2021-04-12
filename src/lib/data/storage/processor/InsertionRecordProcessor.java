package lib.data.storage.processor;

import lib.data.storage.Storage;
import lib.record.Record;
import lib.util.coordinate.CoordinateTranslator;
import lib.util.position.AllInsertionsPosProvider;
import lib.util.position.Position;
import lib.util.position.PositionProvider;

/**
 * TODO
 */
public class InsertionRecordProcessor implements GeneralRecordProcessor {

	private final CoordinateTranslator translator;
	
	private final Storage insStorage;
	
	public InsertionRecordProcessor(
			final CoordinateTranslator translator,
			final Storage insStorage) {
		
		this.translator	= translator;
		this.insStorage	= insStorage;
	}

	@Override
	public void preProcess() {
		// nothing to be done
	}
	
	@Override
	public void process(final Record record) {
		// store insertions
		final PositionProvider insPosProvider = 
				new AllInsertionsPosProvider(record, translator);
		while (insPosProvider.hasNext()) {
			final Position pos = insPosProvider.next();
			insStorage.increment(pos);
		}
	}
	
	@Override
	public void postProcess() {
		// nothing to be done
	}
	
}
