package jacusa.filter.cache;

import java.util.Collection;

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

	public HomopolymerReferenceFilterCache(
			final char c,
			final FilteredDataFetcher<BooleanWrapperFilteredData, BooleanWrapper> filteredDataFetcher,
			final int minLength,
			final SharedCache sharedCache) {

		super(c, filteredDataFetcher, minLength, sharedCache);
	}

	@Override
	public void process(final SAMRecordWrapper recordWrapper) {
		// nothing to be done
	}

	@Override
	public void preProcess() {
		final int windowLength = getCoordinateController().getActive().getLength();
		cacheWindowPosition(0, windowLength, getMinLength());

		// left of window
		int referenceStart 	= getCoordinateController().getActive().getStart();
		referenceStart 		= Math.max(1, referenceStart - (getMinLength() - 1));
		int referenceEnd 	= getCoordinateController().getActive().getStart() + getMinLength();
		cacheReferencePosition(referenceStart, referenceEnd, getMinLength());
		
		// right of window
		referenceStart 		= getCoordinateController().getActive().getEnd() - (getMinLength() - 2);
		referenceEnd		= getCoordinateController().getActive().getEnd() + getMinLength() - 1;
		cacheReferencePosition(referenceStart, referenceEnd, getMinLength());
	}
	
	@Override
	public void postProcess() {
		// nothing to be done
	}
	
	private void cacheWindowPosition(final int windowPositionStart, final int windowPositionEnd, final int minLength) {
		final HomopolymerBuilder builder = new HomopolymerBuilder(windowPositionStart, minLength);
		
		// within window
		for (int position = windowPositionStart; position < windowPositionEnd; position++) {
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

		final Coordinate coordinate = new Coordinate(getCoordinateController().getActive().getContig(), referenceStart, referenceStart);
		for (int position = referenceStart; position <= referenceEnd; position++) {
			coordinate.setPosition(position);
			final Base base = getReferenceProvider().getReferenceBase(coordinate);
			builder.add(base);
		}

		final Collection<Homopolymer> homopolymers = builder.build();
		for (final Homopolymer homopolymer : homopolymers) {
			markRegion(homopolymer.getPosition(), homopolymer.getLength());
		}
	}
	
}