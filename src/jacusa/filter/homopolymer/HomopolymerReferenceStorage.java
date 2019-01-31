package jacusa.filter.homopolymer;

import java.util.HashMap;
import java.util.Map;

import lib.data.DataContainer;
import lib.data.fetcher.FilteredDataFetcher;
import lib.data.filter.BooleanWrapperFilteredData;
import lib.data.storage.container.SharedStorage;
import lib.data.stroage.AbstractStorage;
import lib.data.filter.BooleanWrapper;
import lib.util.coordinate.Coordinate;
import lib.util.position.Position;

/**
 * TODO add comments
 * 
 * Tested in @see jacusa.filter.homopolymer.HomopolymerReferenceFilterCacheTest
 */
public class HomopolymerReferenceStorage extends AbstractStorage {

	// require only one instance for multiple threads
	private static final Map<Coordinate, HomopolymerStorage> COORD2HOMOPOLYMER_STORAGE;
	
	static {
		COORD2HOMOPOLYMER_STORAGE = new HashMap<>();
	}
	
	private final char c;
	private final FilteredDataFetcher<BooleanWrapperFilteredData, BooleanWrapper> filteredDataFetcher; 

	// min length of identical base call to define homopolymer
	private final int minLength;

	private HomopolymerStorage storage;
	
	public HomopolymerReferenceStorage(
			final SharedStorage sharedStorage,
			final char c,
			final FilteredDataFetcher<BooleanWrapperFilteredData, BooleanWrapper> filteredDataFetcher, 
			final int minLength) {

		super(sharedStorage);
		this.c 						= c;
		this.minLength 				= minLength;
		this.filteredDataFetcher 	= filteredDataFetcher;
	}

	@Override
	public void clear() {
		final Coordinate active = getCoordinateController().getActive();
		if (contains(active)) {
			remove(active);
		}
	}

	void updateStorage(final Coordinate coordinate) {
		storage	= get(coordinate);
	}
	
	@Override
	public void increment(Position pos) {
		storage.increment(pos);
	}
	
	@Override
	public void populate(DataContainer dataContainer, int winPos, Coordinate coordinate) {
		storage.populate(dataContainer, winPos, coordinate);
	}
	
	public boolean contains(final Coordinate coordinate) {
		return COORD2HOMOPOLYMER_STORAGE.containsKey(coordinate);
	}
	
	public void remove(final Coordinate coordinate) {
		COORD2HOMOPOLYMER_STORAGE.remove(coordinate);
	}
	
	public HomopolymerStorage add(final Coordinate coordinate) {
		return COORD2HOMOPOLYMER_STORAGE.put(
				coordinate, 
				new HomopolymerStorage(getSharedStorage(), c, filteredDataFetcher, minLength));
	}
	
	public HomopolymerStorage get(final Coordinate coordinate) {
		return COORD2HOMOPOLYMER_STORAGE.get(coordinate);
	}
	
}
