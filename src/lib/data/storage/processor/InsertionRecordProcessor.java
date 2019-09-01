package lib.data.storage.processor;

import lib.recordextended.SAMRecordExtended;

import lib.data.storage.Storage;
import lib.util.coordinate.CoordinateTranslator;
import lib.util.position.AllInsertionsPositionProvider;
import lib.util.position.ConsumingReferencePositionProviderBuilder;
import lib.util.position.Position;
import lib.util.position.PositionProvider;

public class InsertionRecordProcessor implements RecordExtendedPrePostProcessor {

	private final CoordinateTranslator translator;
	
	private final Storage covStorage;
	private final Storage insStorage;
	
	public InsertionRecordProcessor(
			final CoordinateTranslator translator,
			final Storage covStorage,
			final Storage insStorage) {
		
		this.translator	= translator;
		this.covStorage	= covStorage;
		this.insStorage	= insStorage;
	}

	@Override
	public void preProcess() {
		// nothing to be done
	}
	
	@Override
	public void process(final SAMRecordExtended recordExtended) {
		// store total coverage
		final PositionProvider covPosProvider = 
				new ConsumingReferencePositionProviderBuilder(recordExtended, translator).build();
		while (covPosProvider.hasNext()) {
			final Position pos = covPosProvider.next();
			covStorage.increment(pos);
		}
		
		// store insetions
		final PositionProvider insPosProvider = 
				new AllInsertionsPositionProvider(recordExtended, translator);
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
