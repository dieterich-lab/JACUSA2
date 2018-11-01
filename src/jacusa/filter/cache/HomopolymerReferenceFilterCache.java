package jacusa.filter.cache;

import java.util.Collection;

import jacusa.filter.cache.Homopolymer.HomopolymerBuilder;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.cache.container.SharedCache;
import lib.data.cache.fetcher.FilteredDataFetcher;
import lib.data.cache.record.RecordWrapperDataCache;
import lib.data.filter.BooleanWrapperFilteredData;
import lib.data.filter.BooleanWrapper;
import lib.util.Base;

/**
 * TODO add comments
 * 
 * @param 
 */
// <D extends AbstractData & HasBooleanFilterData> 
public class HomopolymerReferenceFilterCache
extends AbstractHomopolymerFilterCache
implements RecordWrapperDataCache {

	private boolean cached;
	
	public HomopolymerReferenceFilterCache(
			final char c,
			final FilteredDataFetcher<BooleanWrapperFilteredData, BooleanWrapper> filteredDataFetcher,
			final int minLength,
			final SharedCache sharedCache) {

		super(c, filteredDataFetcher, minLength, sharedCache);
		cached 				= false;
	}

	@Override
	public void processRecordWrapper(final SAMRecordWrapper recordWrapper) {
		if (! cached) {
			final int windowLength = getCoordinateController().getActive().getEnd() - getCoordinateController().getActive().getStart() + 1; 
			cacheWindowPosition(0, windowLength, getMinLength());
			
			// left of window
			int referenceStart 	= getCoordinateController().getActive().getStart();
			referenceStart 		= Math.min(1, referenceStart - (getMinLength() - 1));
			int referenceEnd 	= getCoordinateController().getActive().getStart() + getMinLength();
			cacheReferencePosition(referenceStart, referenceEnd, getMinLength());
			
			// right of window
			referenceStart 		= getCoordinateController().getActive().getEnd() - (getMinLength() - 2);
			referenceEnd		= getCoordinateController().getActive().getEnd() + getMinLength() - 1;
			cacheReferencePosition(referenceStart, referenceEnd, getMinLength());
		}
	}

	private void cacheWindowPosition(final int windowPositionStart, final int windowPositionEnd, final int minLength) {
		final HomopolymerBuilder builder = new HomopolymerBuilder(windowPositionStart, minLength);
		
		// within window
		for (int position = windowPositionStart; position <= windowPositionEnd; position++) {
			final Base base = getReferenceProvider().getReferenceBase(position);
			builder.add(base);
		}
		final Collection<Homopolymer> homopolymers = builder.build();
		for (final Homopolymer homopolymer : homopolymers) {
			markRegion(homopolymer.getPosition(), homopolymer.getLength());
		}
	}

	private void cacheReferencePosition(final int referenceStart, final int referenceEnd, final int minLength) {
		final HomopolymerBuilder builder = new HomopolymerBuilder(referenceStart, minLength);

		for (int position = referenceStart; position <= referenceEnd; position++) {
			final Base base = getReferenceProvider().getReferenceBase(position);
			builder.add(base);
		}

		final Collection<Homopolymer> homopolymers = builder.build();
		for (final Homopolymer homopolymer : homopolymers) {
			markRegion(homopolymer.getPosition(), homopolymer.getLength());
		}
	}
	
}