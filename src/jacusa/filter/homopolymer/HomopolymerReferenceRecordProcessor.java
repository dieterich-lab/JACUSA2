package jacusa.filter.homopolymer;

import java.util.Collection;

import jacusa.filter.homopolymer.Homopolymer.HomopolymerBuilder;
import lib.data.storage.container.ReferenceProvider;
import lib.data.storage.processor.RecordExtendedPrePostProcessor;
import lib.util.Base;
import lib.util.coordinate.Coordinate;
import lib.util.coordinate.CoordinateController;
import lib.util.coordinate.OneCoordinate;
import lib.recordextended.SAMRecordExtended;

/**
 * TODO add comments
 * 
 * 
 * Tested in @see jacusa.filter.homopolymer.HomopolymerReferenceFilterCacheTest
 */
public class HomopolymerReferenceRecordProcessor implements RecordExtendedPrePostProcessor {

	private final int minLength;
	private final HomopolymerReferenceStorage storage;
	
	public HomopolymerReferenceRecordProcessor(
			final int minLength,
			final HomopolymerReferenceStorage storage) {
		
		this.minLength 	= minLength;
		this.storage 	= storage;
	}
	
	@Override
	public void process(final SAMRecordExtended recordExtended) {
		// nothing to be done
	}

	private CoordinateController getCoordinateController() {
		return storage.getSharedStorage().getCoordinateController();
	}

	private ReferenceProvider getReferenceProvider() {
		return storage.getReferenceProvider();
	}
	
	private int getMinLength() {
		return minLength;
	}
	
	@Override
	public void preProcess() {
		final Coordinate active = getCoordinateController().getActive().copy();
		if (storage.contains(active)) {
			return;
		}
		final HomopolymerStorage tmpStorage = storage.add(active);
		
		final int windowLength = getCoordinateController().getActive().getLength();
		// cache homopolymer within window
		tmpStorage.cacheWindowPosition(0, windowLength, getMinLength());

		// cache left of window
		int referenceStart 	= getCoordinateController().getActive().getStart();
		referenceStart 		= Math.max(1, referenceStart - (getMinLength() - 1));
		int referenceEnd 	= getCoordinateController().getActive().getStart() + getMinLength() - 1;
		cacheReferencePosition(referenceStart, referenceEnd, getMinLength(), tmpStorage);
		
		// cache right of window
		referenceStart 		= getCoordinateController().getActive().getEnd() - (getMinLength() - 1);
		referenceEnd		= getCoordinateController().getActive().getEnd() + getMinLength() - 1;
		cacheReferencePosition(referenceStart, referenceEnd, getMinLength(), tmpStorage);
	}
	
	@Override
	public void postProcess() {
		// nothing to be done
	}

	/**
	 * Search and cache homopolymers in a ref. region 
	 * @param referenceStart
	 * @param referenceEnd
	 * @param minLength
	 */
	private void cacheReferencePosition(
			final int referenceStart, final int referenceEnd, final int minLength,
			final HomopolymerStorage tmpStorage) {
		
		final HomopolymerBuilder builder = new HomopolymerBuilder(referenceStart, minLength);
		// needed to distinguish from window coordinates
		final Coordinate coordinate = new OneCoordinate(getCoordinateController().getActive().getContig(), referenceStart, referenceStart);
		for (int refPos = referenceStart; refPos <= referenceEnd; refPos++) {
			coordinate.set1Position(refPos);
			final Base base = getReferenceProvider().getReferenceBase(coordinate);
			builder.add(base);
		}
		// and build homopolymers
		final Collection<Homopolymer> homopolymers = builder.build();
		
		for (final Homopolymer homopolymer : homopolymers) {
			// mark regions as homopolymers
			tmpStorage.increment(homopolymer.getPosition(), homopolymer.getLength());
		}
	}
	
}
