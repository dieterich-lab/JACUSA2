package jacusa.filter.homopolymer;

import java.util.HashMap;
import java.util.Map;

import lib.data.DataContainer;
import lib.data.fetcher.FilteredDataFetcher;
import lib.data.filter.BooleanFilteredData;
import lib.data.storage.AbstractStorage;
import lib.data.storage.container.SharedStorage;
import lib.data.filter.BooleanData;
import lib.util.coordinate.Coordinate;
import lib.util.position.Position;

/**
 * Class the enables storing reference based homopolymer information from 
 * multiple instances each condition and replicate have its own instance. 
 * The reference sequence is for all identical. Compute homopolymer once for one 
 * instance and reuse for others. 
 * Needs optimization.
 * 
 * Tested in @see jacusa.filter.homopolymer.HomopolymerReferenceFilterCacheTest
 */
public class HomopolymerReferenceStorage extends AbstractStorage {

	// require only one instance for multiple threads
	private static final Map<Coordinate, HomopolymerStorage> COORD2HOMOPOLYMER_STORAGE;
	private static final Map<Coordinate, Integer> COORD2COUNT;
	
	static {
		COORD2HOMOPOLYMER_STORAGE 	= new HashMap<>(30);
		COORD2COUNT					= new HashMap<>(30); 
	}
	
	private final char id;
	private final FilteredDataFetcher<BooleanFilteredData, BooleanData> filteredDataFetcher; 

	// min length of identical base call to define homopolymer
	private final int minLength;
	private final int bamFileCount;

	private HomopolymerStorage storage;
	
	public HomopolymerReferenceStorage(
			final SharedStorage sharedStorage,
			final char id,
			final FilteredDataFetcher<BooleanFilteredData, BooleanData> filteredDataFetcher, 
			final int minLength,
			final int bamFiles) {

		super(sharedStorage);
		this.id 					= id;
		this.filteredDataFetcher 	= filteredDataFetcher;
		this.minLength 				= minLength;
		this.bamFileCount			= bamFiles;
	}

	@Override
	public void clear() {
		final Coordinate active = getCoordinateController().getActive();
		int count = increment(active);
		if (count == bamFileCount) {
			storage = null;
			remove(active);
		}
	}

	HomopolymerStorage updateStorage(final Coordinate coordinate) {
		if (! contains(coordinate)) {
			storage = add(coordinate);
		} else {
			storage	= get(coordinate);			
		}
		increment(coordinate);
		return storage;
	}
	
	int increment(final Coordinate coordinate) {
		int count = 0;
		if (COORD2COUNT.containsKey(coordinate)) {
			count = COORD2COUNT.get(coordinate);
		}
		++count;
		COORD2COUNT.put(coordinate, count);
		return count;
	}
	
	@Override
	public void increment(Position pos) {
		storage.increment(pos);
	}
	
	@Override
	public void populate(DataContainer dataContainer, int winPos, Coordinate coordinate) {
		if (storage != null) {
			storage.populate(dataContainer, winPos, coordinate);
		}
	}
		
	public boolean contains(final Coordinate coordinate) {
		return COORD2HOMOPOLYMER_STORAGE.containsKey(coordinate);
	}
	
	void remove(final Coordinate coordinate) {
		COORD2HOMOPOLYMER_STORAGE.remove(coordinate);
		COORD2COUNT.remove(coordinate);
	}
	
	HomopolymerStorage add(final Coordinate coordinate) {
		final HomopolymerStorage storage = new HomopolymerStorage(
				getSharedStorage(), id, filteredDataFetcher, minLength);
		COORD2HOMOPOLYMER_STORAGE.put(coordinate, storage);
		return storage;
	}
	
	public HomopolymerStorage get(final Coordinate coordinate) {
		return COORD2HOMOPOLYMER_STORAGE.get(coordinate);
	}
	
}
