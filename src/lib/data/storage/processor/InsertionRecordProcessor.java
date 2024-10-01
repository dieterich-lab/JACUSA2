package lib.data.storage.processor;

import lib.data.storage.Storage;
import lib.record.Record;
import lib.util.coordinate.CoordinateTranslator;
import lib.util.position.AllInsertionsPosProvider;
import lib.util.position.Position;
import lib.util.position.PositionProvider;

/**
 * TODO add documentation
 */
public class InsertionRecordProcessor implements GeneralRecordProcessor {

	private final CoordinateTranslator translator;
	
	private final Storage insStorage;
	private final boolean onlyStart;
	
	public InsertionRecordProcessor(
			final CoordinateTranslator translator,
			final Storage insStorage,
			final boolean onlyStart) {
		
		this.translator	= translator;
		this.insStorage	= insStorage;
		this.onlyStart	= onlyStart;
	}

	@Override
	public void preProcess() {
		// nothing to be done
	}
	
	@Override
	public void process(final Record record) {
		// store insertions
		final PositionProvider insPosProvider = 
				new AllInsertionsPosProvider(record, translator, onlyStart);
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
