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
 * This class process a read and identifies homopolymers within the aligned region. Base call quality
 * and base calls from insertions are ignored.
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
		// ignore all non aligned positions such as insertions
		final PositionProvider positionProvider = new AllAlignmentBlocksPositionProvider(
				recordExtended, getTranslator());
		
		if (! positionProvider.hasNext()) {
			return;
 		}
		// get the first position and base call to start the first homopolymer
		Position pos 		= positionProvider.next();
		final int refPos 	= pos.getReferencePosition();
		final HomopolymerBuilder builder = new HomopolymerBuilder(refPos, getMinLength());
		
		// continue search for homopolymers as long as there are aligned position
		while (positionProvider.hasNext()) {
			pos = positionProvider.next();
			builder.add(pos.getReadBaseCall());
		}
		
		// create collection of identified homopolymers and save them in storage
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