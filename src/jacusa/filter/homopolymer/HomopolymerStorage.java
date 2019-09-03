package jacusa.filter.homopolymer;

import java.util.Arrays;
import java.util.Collection;

import jacusa.filter.homopolymer.Homopolymer.HomopolymerBuilder;
import lib.data.DataContainer;
import lib.data.fetcher.FilteredDataFetcher;
import lib.data.filter.BooleanFilteredData;
import lib.data.storage.AbstractStorage;
import lib.data.storage.container.SharedStorage;
import lib.data.filter.BooleanData;
import lib.util.Base;
import lib.util.coordinate.Coordinate;
import lib.util.position.Position;

/**
 * Simple window based storage indicating if at a specific window position a homopolymer could be
 * identified.
 */
public class HomopolymerStorage extends AbstractStorage {
	
	private final char id;
	// defines where to look for boolean information in dataContainer
	private final FilteredDataFetcher<BooleanFilteredData, BooleanData> filteredDataFetcher; 

	// min length of identical base call to define homopolymer
	private final int minLength;
	
	// indices of position in window is a homopolymer
	private final boolean[] isHomopolymer;
	
	public HomopolymerStorage(
			final SharedStorage sharedStorage,
			final char id,
			final FilteredDataFetcher<BooleanFilteredData, BooleanData> filteredDataFetcher,
			final int minLength) {

		super(sharedStorage);

		this.id						= id;
		this.filteredDataFetcher 	= filteredDataFetcher;
		this.minLength 				= minLength;
		
		final int size				= sharedStorage.getCoordinateController().getActiveWindowSize();
		isHomopolymer 				= new boolean[size];
	}
	
	@Override
	public void increment(Position pos) {
		isHomopolymer[pos.getWindowPosition()] = true;
	}
	
	public void increment(final int refPos, final int length) {
		for (int i = 0; i < length; ++i) {
			final int winPos = getCoordinateController()
				.getCoordinateTranslator().ref2winPos(refPos + i);
			if (winPos >= 0) {
				isHomopolymer[winPos] = true;
			}
		}
	}
	
	@Override
	public void clear() {
		Arrays.fill(isHomopolymer, false);
	}	
	
	public int getMinLength() {
		return minLength;
	}

	@Override
	public void populate(DataContainer container, int winPos, Coordinate coordinate) {
		if (isHomopolymer[winPos]) {
			filteredDataFetcher.fetch(container).add(
					id, new BooleanData(isHomopolymer[winPos]));
		}
	}
	
	/**
	 * Search and cache homopolymers within windows
	 * @param windowPositionStart
	 * @param windowPositionEnd
	 * @param minLength
	 */
	void cacheWindowPosition(
			final int windowPositionStart, final int windowPositionEnd, final int minLength) {

		final HomopolymerBuilder builder = new HomopolymerBuilder(windowPositionStart, minLength);
		// collect bases within window...
		for (int winPos = windowPositionStart; winPos < windowPositionEnd; winPos++) {
			final Base base = getReferenceProvider().getReferenceBase(winPos);
			builder.add(base);
		}
		// and build homopolymers
		final Collection<Homopolymer> homopolymers = builder.build();
		// mark regions as homopolymers
		final int refPositionStart = getCoordinateController().getActive().get1Start();
		for (final Homopolymer homopolymer : homopolymers) {
			increment(
					refPositionStart + homopolymer.getPosition(), 
					homopolymer.getLength());
		}
	}
	
}
