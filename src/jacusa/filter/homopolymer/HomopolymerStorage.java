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
 * TODO add comments
 * 
 * @param 
 */
public class HomopolymerStorage extends AbstractStorage {
	
	private final char c;
	private final FilteredDataFetcher<BooleanFilteredData, BooleanData> filteredDataFetcher; 

	// min length of identical base call to define homopolymer
	private final int minLength;
	
	// indices of position in window is a homopolymer
	private final boolean[] isHomopolymer;
	
	public HomopolymerStorage(
			final SharedStorage sharedStorage,
			final char c,
			final FilteredDataFetcher<BooleanFilteredData, BooleanData> filteredDataFetcher,
			final int minLength) {

		super(sharedStorage);

		this.c						= c;
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
		final int winPos = getCoordinateController()
				.getCoordinateTranslator().reference2windowPosition(refPos);
		Arrays.fill(isHomopolymer, winPos, winPos + length, true);
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
					c, new BooleanData(isHomopolymer[winPos]));
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