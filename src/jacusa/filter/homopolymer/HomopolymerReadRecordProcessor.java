package jacusa.filter.homopolymer;

import java.util.Collection;

import jacusa.filter.homopolymer.Homopolymer.HomopolymerBuilder;
import lib.data.storage.processor.RecordExtendedPrePostProcessor;
import lib.util.coordinate.CoordinateTranslator;
import lib.util.position.AllAlignmentBlocksPositionProvider;
import lib.util.position.Position;
import lib.util.position.PositionProvider;
import lib.recordextended.SAMRecordExtended;

/**
 * TODO add comments
 * 
 * Tested in @see test.jacusa.filter.homopolymer.HomopolymerReadFilterCacheTest
 */
public class HomopolymerReadRecordProcessor implements RecordExtendedPrePostProcessor {

	private final int minLength;
	
	private final HomopolymerStorage storage;
	
	public HomopolymerReadRecordProcessor(
			final int minLength,
			final HomopolymerStorage storage) {
		this.minLength	= minLength;
		this.storage 	= storage;
	}
	
	@Override
	public void preProcess() {
		// nothing to be done
	}
	
	@Override
	public void postProcess() {
		// nothing to be done
	}
	
	@Override
	public void process(final SAMRecordExtended recordExtended) {
		PositionProvider positionProvider = new AllAlignmentBlocksPositionProvider(
				recordExtended, getTranslator());
		
		if (! positionProvider.hasNext()) {
			return;
 		}
		Position pos 		= positionProvider.next();
		final int refPos 	= pos.getReferencePosition();
		final HomopolymerBuilder builder = new HomopolymerBuilder(refPos, getMinLength());
		
		while (positionProvider.hasNext()) {
			pos = positionProvider.next();
			builder.add(pos.getReadBaseCall());
		}
		
		final Collection<Homopolymer> homopolymers = builder.build();
		for (final Homopolymer homopolymer : homopolymers) {
			storage.increment(homopolymer.getPosition(), homopolymer.getLength());
		}
	}

	private CoordinateTranslator getTranslator() {
		return storage.getCoordinateController().getCoordinateTranslator();
	}

	public int getMinLength() {
		return minLength;
	}
	
}