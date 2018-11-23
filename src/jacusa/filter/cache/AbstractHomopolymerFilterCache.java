package jacusa.filter.cache;

import java.util.Arrays;

import lib.data.DataTypeContainer;
import lib.data.adder.AbstractDataContainerAdder;
import lib.data.cache.container.SharedCache;
import lib.data.cache.fetcher.FilteredDataFetcher;
import lib.data.cache.record.RecordWrapperProcessor;
import lib.data.filter.BooleanWrapperFilteredData;
import lib.data.filter.BooleanWrapper;
import lib.util.coordinate.Coordinate;
import lib.util.coordinate.CoordinateController.WindowPositionGuard;

/**
 * TODO add comments
 * 
 * @param 
 */
// <D extends AbstractData & HasBooleanFilterData> 
public abstract class AbstractHomopolymerFilterCache
extends AbstractDataContainerAdder
implements RecordWrapperProcessor {

	private final char c;
	private final FilteredDataFetcher<BooleanWrapperFilteredData, BooleanWrapper> filteredDataFetcher; 

	// min length of identical base call to define homopolymer
	private final int minLength;
	
	// indices of position in window is a homopolymer
	private final boolean[] isHomopolymer;
	
	public AbstractHomopolymerFilterCache(
			final char c,
			final FilteredDataFetcher<BooleanWrapperFilteredData, BooleanWrapper> filteredDataFetcher,
			final int minLength,
			final SharedCache sharedCache) {

		super(sharedCache);

		this.c				= c;
		this.filteredDataFetcher = filteredDataFetcher;
		this.minLength 		= minLength;
		isHomopolymer 		= new boolean[sharedCache.getCoordinateController().getActiveWindowSize()];
	}
	
	/**
	 * Helper method. Marks a region within a window defined by firstReferencePosition and length. 
	 * 
	 * @param firstReferencePosition	start position of region
	 * @param length					length of region (non-inclusive)
	 */
	protected void markRegion(final int firstReferencePosition, final int length) {
		final WindowPositionGuard windowPositionGuard = 
				getCoordinateController().convert(firstReferencePosition, length);
		for (int i = 0; i < windowPositionGuard.getLength(); ++i) {
			isHomopolymer[windowPositionGuard.getWindowPosition() + i] = true;
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
	public void populate(DataTypeContainer container, Coordinate coordinate) {
		final int windowPosition = getCoordinateController().getCoordinateTranslator().convert2windowPosition(coordinate);
		filteredDataFetcher.fetch(container).add(
				c, 
				new BooleanWrapper(isHomopolymer[windowPosition]));
	}
	
}