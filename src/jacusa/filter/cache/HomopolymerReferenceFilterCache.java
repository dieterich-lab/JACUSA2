package jacusa.filter.cache;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import jacusa.filter.cache.Homopolymer.HomopolymerBuilder;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.cache.container.SharedCache;
import lib.data.cache.fetcher.FilteredDataFetcher;
import lib.data.cache.record.RecordWrapperProcessor;
import lib.data.filter.BooleanWrapperFilteredData;
import lib.data.filter.BooleanWrapper;
import lib.util.Base;
import lib.util.coordinate.Coordinate;

/**
 * TODO add comments
 * 
 * @param 
 */
// <D extends AbstractData & HasBooleanFilterData> 
public class HomopolymerReferenceFilterCache
extends AbstractHomopolymerFilterCache
implements RecordWrapperProcessor {
	
	private static final Map<Coordinate, IsHomopolyerHelper> COORD2IS_HOMOPOLYMER;
	
	static {
		COORD2IS_HOMOPOLYMER = new HashMap<>();
	}

	// indices of position in window is a homopolymer
	private boolean[] isHomopolymer;
	
	public HomopolymerReferenceFilterCache(
			final char c,
			final FilteredDataFetcher<BooleanWrapperFilteredData, BooleanWrapper> filteredDataFetcher,
			final int minLength,
			final SharedCache sharedCache) {

		super(c, filteredDataFetcher, minLength, sharedCache);
	}

	@Override
	protected boolean[] getIsHomopolymer() {
		return isHomopolymer;
	}
	
	@Override
	public void clear() {
		isHomopolymer = null;
	}
	
	@Override
	public void process(final SAMRecordWrapper recordWrapper) {
		// nothing to be done
	}

	@Override
	public void preProcess() {
		final Coordinate active = new Coordinate(getCoordinateController().getActive());
		if (! COORD2IS_HOMOPOLYMER.containsKey(active)) {
			final IsHomopolyerHelper helper = new IsHomopolyerHelper(new boolean[getCoordinateController().getActiveWindowSize()]);
			COORD2IS_HOMOPOLYMER.put(active, helper);
		}
		isHomopolymer = COORD2IS_HOMOPOLYMER.get(active).get();
		
		final int windowLength = getCoordinateController().getActive().getLength();
		// cache homopolymer within window
		cacheWindowPosition(0, windowLength, getMinLength());

		// cache left of window
		int referenceStart 	= getCoordinateController().getActive().getStart();
		referenceStart 		= Math.max(1, referenceStart - (getMinLength() - 1));
		int referenceEnd 	= getCoordinateController().getActive().getStart() + getMinLength() - 1;
		cacheReferencePosition(referenceStart, referenceEnd, getMinLength());
		
		// cache right of window
		referenceStart 		= getCoordinateController().getActive().getEnd() - (getMinLength() - 1);
		referenceEnd		= getCoordinateController().getActive().getEnd() + getMinLength() - 1;
		cacheReferencePosition(referenceStart, referenceEnd, getMinLength());
	}
	
	@Override
	public void postProcess() {
		final Coordinate active = new Coordinate(getCoordinateController().getActive());
		if (COORD2IS_HOMOPOLYMER.containsKey(active)) {
			COORD2IS_HOMOPOLYMER.remove(active);
		}
	}
	
	/**
	 * Search and cache homopolymers within windows
	 * @param windowPositionStart
	 * @param windowPositionEnd
	 * @param minLength
	 */
	private void cacheWindowPosition(final int windowPositionStart, final int windowPositionEnd, final int minLength) {
		final HomopolymerBuilder builder = new HomopolymerBuilder(windowPositionStart, minLength);
		// collect bases within window...
		for (int winPos = windowPositionStart; winPos < windowPositionEnd; winPos++) {
			final Base base = getReferenceProvider().getReferenceBase(winPos);
			builder.add(base);
		}
		// and build homopolymers
		final Collection<Homopolymer> homopolymers = builder.build();
		// mark regions as homopolymers
		for (final Homopolymer homopolymer : homopolymers) {
			markRegion(homopolymer.getPosition(), homopolymer.getLength());
		}
	}

	/**
	 * Search and cache homopolymers in a ref. region 
	 * @param referenceStart
	 * @param referenceEnd
	 * @param minLength
	 */
	private void cacheReferencePosition(final int referenceStart, final int referenceEnd, final int minLength) {
		final HomopolymerBuilder builder = new HomopolymerBuilder(referenceStart, minLength);
		// needed to distinguish from window coordinates
		final Coordinate coordinate = new Coordinate(getCoordinateController().getActive().getContig(), referenceStart, referenceStart);
		for (int refPos = referenceStart; refPos <= referenceEnd; refPos++) {
			coordinate.setPosition(refPos);
			final Base base = getReferenceProvider().getReferenceBase(coordinate);
			builder.add(base);
		}
		// and build homopolymers
		final Collection<Homopolymer> homopolymers = builder.build();
		for (final Homopolymer homopolymer : homopolymers) {
			// mark regions as homopolymers
			markRegion(homopolymer.getPosition(), homopolymer.getLength());
		}
	}

	private class IsHomopolyerHelper {
		
		private final boolean[] isHomopolymer;
		
		public IsHomopolyerHelper(final boolean[] isHomopolymer) {
			this.isHomopolymer = isHomopolymer;
		}

		public boolean[] get() {
			return isHomopolymer;
		}
		
	}
	
	
}
