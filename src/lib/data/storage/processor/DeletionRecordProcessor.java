package lib.data.storage.processor;

import lib.recordextended.SAMRecordExtended;

import lib.data.storage.Storage;
import lib.util.coordinate.CoordinateTranslator;
import lib.util.position.AllDeletionsPositionProvider;
import lib.util.position.ConsumingReferencePositionProviderBuilder;
import lib.util.position.Position;
import lib.util.position.PositionProvider;

public class DeletionRecordProcessor implements RecordExtendedPrePostProcessor {

	private final CoordinateTranslator translator;
	
	private final Storage covStorage;
	private final Storage delStorage;
	
	public DeletionRecordProcessor(
			final CoordinateTranslator translator,
			final Storage covStorage,
			final Storage delStorage) {
		
		this.translator	= translator;
		this.covStorage	= covStorage;
		this.delStorage	= delStorage;
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
		
		// store deletions
		final PositionProvider delPosProvider = 
				new AllDeletionsPositionProvider(recordExtended, translator);
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
