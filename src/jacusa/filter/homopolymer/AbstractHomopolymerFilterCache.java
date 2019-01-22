package jacusa.filter.homopolymer;

import lib.data.DataTypeContainer;
import lib.data.adder.AbstractDataContainerPopulator;
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
public abstract class AbstractHomopolymerFilterCache
extends AbstractDataContainerPopulator
implements RecordWrapperProcessor {

	private final char c;
	private final FilteredDataFetcher<BooleanWrapperFilteredData, BooleanWrapper> filteredDataFetcher; 

	// min length of identical base call to define homopolymer
	private final int minLength;
	
	public AbstractHomopolymerFilterCache(
			final char c,
			final FilteredDataFetcher<BooleanWrapperFilteredData, BooleanWrapper> filteredDataFetcher,
			final int minLength,
			final SharedCache sharedCache) {

		super(sharedCache);

		this.c				= c;
		this.filteredDataFetcher = filteredDataFetcher;
		this.minLength 		= minLength;
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
			getIsHomopolymer()[windowPositionGuard.getWindowPosition() + i] = true;
		}
	}

	protected abstract boolean[] getIsHomopolymer();
	
	public int getMinLength() {
		return minLength;
	}

	@Override
	public void populate(DataTypeContainer container, Coordinate coordinate) {
		final int windowPosition = getCoordinateController()
				.getCoordinateTranslator()
				.convert2windowPosition(coordinate);
		if (getIsHomopolymer() != null) {
			filteredDataFetcher.fetch(container).add(
					c, 
					new BooleanWrapper(getIsHomopolymer()[windowPosition]));
		}
	}
	
}