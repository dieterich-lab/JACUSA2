package test.jacusa.filter.homopolymer;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.stream.Stream;

import org.junit.jupiter.params.provider.Arguments;

import jacusa.filter.homopolymer.HomopolymerReadFilterCache;
import lib.data.DataType;
import lib.data.DataTypeContainer;
import lib.data.cache.container.SharedCache;
import lib.data.cache.fetcher.DefaultFilteredDataFetcher;
import lib.data.cache.fetcher.FilteredDataFetcher;
import lib.data.filter.BooleanWrapperFilteredData;
import lib.data.filter.BooleanWrapper;
import lib.data.has.LibraryType;
import lib.util.Base;
import lib.util.coordinate.Coordinate;

/**
 * Test @see test.jacusa.filter.homopolymer.HomopolymerReadFilterCache
 */

class HomopolymerReadFilterCacheTest implements RecordWrapperProcessorTest<String> {
	
	private final char C = 'X';
	
	private final FilteredDataFetcher<BooleanWrapperFilteredData, BooleanWrapper> filteredDataFetcher;

	public HomopolymerReadFilterCacheTest() {
		filteredDataFetcher = new DefaultFilteredDataFetcher<>(DataType.F_BOOLEAN);
	}
	
	/*
	 * @CsvSource(delimiter = ' ', value = {
			"UNSTRANDED ACGTACGT 3 2 4 00000000",
			"UNSTRANDED ACGAACGT 3 2 4 00011000",
			"RF_FIRSTSTRAND ACGAACGT 3 2 8 00011000",
			"FR_SECONDSTRAND ACGAACGT 3 3 8 00000000"
	})
	 */
	Stream<Arguments> testAddRecordWrapper() {
		return Stream.of(
				Arguments.of(
						)
				);
	}
	
	
	
	@Override
	public DataTypeContainer createDataTypeContainer(Coordinate coordinate, LibraryType libraryType, Base refBase) {
		// create data type container that will store homopolymer info
		return getBuilderFactory().createBuilder(coordinate, libraryType)
				.withReferenceBase(refBase)
				.with(filteredDataFetcher.getDataType())
				.build();
	}
	
	@Override
	public void assertEqual(
			final int windowPosition, final Coordinate currentCoordinate, 
			final DataTypeContainer container, final String expectedStr) {
				
		final boolean expected = str2booleanArr(expectedStr)[windowPosition];
		final boolean actual = filteredDataFetcher.fetch(container).get(C).getValue();
		assertEquals(
				expected, actual, 
				"Error in current: " + currentCoordinate.toString() + "; " +
						"expected: " + expected + "; actual: " + actual + "; ");
	}
	
	HomopolymerReadFilterCache createTestInstance(
			final int minHomopolymerLength,
			final SharedCache sharedCache) {
		return new HomopolymerReadFilterCache(
				C, 
				filteredDataFetcher, 
				minHomopolymerLength, 
				sharedCache );
	}
	
	private boolean[] str2booleanArr(final String s) {
		final boolean[] res = new boolean[s.length()];
		Arrays.fill(res, false);

		int index = s.indexOf("1");
		while (index >= 0) {
			res[index] = true;
			index = s.indexOf("1", index + 1);
		}
		return res;
	}

	/*
	public int getWindowPosition(final int windowIndex, final Coordinate current) {
		int windowPosition = coordinateController.getCoordinateTranslator().convert2windowPosition(current);
		if (LibraryType.isStranded(libraryType)) {
			windowPosition = 2 * windowPosition + windowIndex * 2 * activeWindowSize;
			if (current.getStrand() == STRAND.REVERSE) {
				windowPosition++;
			}
		} else {
			windowPosition += windowIndex * activeWindowSize;
		}

		return windowPosition;
	}
	*/
	
}
