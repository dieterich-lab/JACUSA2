package test.lib.data;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import lib.data.DataContainer;
import lib.data.DefaultDataContainer;
import lib.data.ParallelData;
import lib.data.count.basecall.BaseCallCount;
import lib.util.Base;
import lib.util.LibraryType;
import lib.util.coordinate.Coordinate;
import lib.util.coordinate.OneCoordinate;
import test.lib.data.count.basecall.BaseCallCountListArgumentConverter;
import test.lib.util.BaseSetArgumentConverter;
import test.lib.util.coordinate.OneCoordinateArgumentConverter;

// JUNIT: A
class ParallelDataTest {

	@ParameterizedTest(name = "For coord.: {0}, Lib.: {1}, and Ref.: {2} variants: {3} are expected")
	@CsvSource({
		"1:1-2:+,UNSTRANDED,A,CGT",
		"1:1-2:+,RF_FIRSTSTRAND,A,CGT",
		"1:1-2:+,FR_SECONDSTRAND,A,CGT",
		"1:1-2:-,UNSTRANDED,A,ACG",
		"1:1-2:-,RF_FIRSTSTRAND,A,ACG",
		"1:1-2:-,FR_SECONDSTRAND,A,ACG",
	})
	void testGetNonReferenceBases(
			@ConvertWith(OneCoordinateArgumentConverter.class) Coordinate coordinate, 
			LibraryType libraryType, 
			Base referenceBase, 
			@ConvertWith(BaseSetArgumentConverter.class) Set<Base> expected) {
		final Set<Base> actual = ParallelData.getNonReferenceBases(referenceBase);
		assertEquals(expected, actual);
	}
	
	@ParameterizedTest(name = "For base call counts {0} and observed alleles {1} the expected variant bases should be {2}")
	@CsvSource(
			delimiter = '\t',
			value = {
					"1,0,0,0;1,1,0,0;1,0,1,0;1,0,0,1	CGT	CGT",
					
					"1,0,0,0;0,1,0,0;0,0,1,0;0,0,0,1	A	A",
					"1,0,0,0;0,1,0,0;0,0,1,0;0,0,0,1	AC	AC",
					"1,0,0,0;0,1,0,0;0,0,1,0;0,0,0,1	ACG	ACG",
					"1,0,0,0;0,1,0,0;0,0,1,0;0,0,0,1	ACGT	ACGT"
					})
	void testGetVariantBases(
			@ConvertWith(BaseCallCountListArgumentConverter.class) List<BaseCallCount> bccs,
			@ConvertWith(BaseSetArgumentConverter.class) Set<Base> observedAlleles,
			@ConvertWith(BaseSetArgumentConverter.class) Set<Base> expected) {
		
		final Set<Base> actual = ParallelData.getVariantBases(
				observedAlleles,
				bccs);
		assertEquals(expected, actual);
	}

	
	@ParameterizedTest(name = "Expected common coordinate {1}")
	@MethodSource("testGetCommonCoordinate")
	void testGetCommonCoordinate(
			List<DataContainer> containers, 
			@ConvertWith(OneCoordinateArgumentConverter.class) Coordinate expected) {
		final Coordinate actual = ParallelData.getCommonCoordinate(containers);
		assertEquals(expected, actual);
	}

	/*
	 * Format:
	 * 1.	List<DataContainer>
	 * 1a.	List<Coordinate>
	 * 1b.	List<LibraryType>
	 * 2.	Coordinate(Expected)
	 */
	static Stream<Arguments> testGetCommonCoordinate() {
		final String lt = LibraryType.UNSTRANDED.toString();
		return Stream.of(
				// 1 coordinate, all lib-types
				Arguments.of(
						createDataContainers(
								Arrays.asList("1:1-2:."),
								Collections.nCopies(1, lt)),
						"1:1-2:."),
				Arguments.of(
						createDataContainers(
								Arrays.asList("1:1-2:+"),
								Collections.nCopies(1, lt)),
						"1:1-2:+"),
				Arguments.of(
						createDataContainers(
								Arrays.asList("1:1-2:-"),
								Collections.nCopies(1, lt)),
						"1:1-2:-"),
				// 2 coordinate, all lib-types
				Arguments.of(
						createDataContainers(
								Arrays.asList("1:1-2:.", "1:1-2:."),
								Collections.nCopies(2, lt)),
						"1:1-2:."),
				Arguments.of(
						createDataContainers(
								Arrays.asList("1:1-2:+", "1:1-2:+"),
								Collections.nCopies(2, lt)),
						"1:1-2:+"),
				Arguments.of(
						createDataContainers(
								Arrays.asList("1:1-2:-", "1:1-2:-"),
								Collections.nCopies(2, lt)),
						"1:1-2:-")
				);
	}

	@Test
	void testGetCommonCoordinateFail() {
		final String lt = LibraryType.UNSTRANDED.toString();
		final Class<IllegalStateException> expectedType = IllegalStateException.class;
		
		// strands don't match
		assertThrows(
				expectedType, 
				() -> {
					final List<DataContainer> containers = 
							createDataContainers(
									Arrays.asList("1:1-2:.", "1:1-2:+"),
									Collections.nCopies(2, lt));
					ParallelData.getCommonCoordinate(containers);
				});

		// strands don't match
		assertThrows(
				expectedType, 
				() -> {
					final List<DataContainer> containers = 
							createDataContainers(
									Arrays.asList("1:1-2:-", "1:1-2:+"),
									Collections.nCopies(2, lt));
					ParallelData.getCommonCoordinate(containers);
				});
		
		// != contigs
		assertThrows(
				expectedType, 
				() -> {
					final List<DataContainer> containers = 
							createDataContainers(
									Arrays.asList("1:1-2:.", "2:1-2:."),
									Collections.nCopies(2, lt));
					ParallelData.getCommonCoordinate(containers);
				});

		// don't overlap
		assertThrows(
				expectedType, 
				() -> {
					final List<DataContainer> containers = 
							createDataContainers(
									Arrays.asList("1:1-2:.", "1:3-4:."),
									Collections.nCopies(2, lt));
					ParallelData.getCommonCoordinate(containers);
				});
	}

	static List<DataContainer> createDataContainers(
			final List<String> coordinateStrings, 
			final List<String> libraryTypeStrings) {
		
		assertEquals(coordinateStrings.size(), libraryTypeStrings.size());
		final int length = coordinateStrings.size();
		return IntStream.range(0, length)
			.mapToObj(i -> createDataContainer(coordinateStrings.get(i), libraryTypeStrings.get(i)))
			.collect(Collectors.toList());
	}
	
	
	static DataContainer createDataContainer(
			final Coordinate.AbstractParser parser, final String coordinateStr, 
			final String libraryTypeStr) {
		
		final Coordinate coordinate 	= parser.parse(coordinateStr);
		final LibraryType libraryType 	= LibraryType.valueOf(libraryTypeStr);
		return new DefaultDataContainer.Builder(coordinate, libraryType).build();
	}
	
	static DataContainer createDataContainer(
			final String coordinateStr, final String libraryTypeStr) {
		
		return createDataContainer(new OneCoordinate.Parser(), coordinateStr, libraryTypeStr);
	}
	
	@ParameterizedTest(name = "Expected common library type {1}")
	@MethodSource("testGetCommonLibraryType")
	void testGetCommonLibraryType(
			List<DataContainer> containers,
			LibraryType expected) {
		
		final LibraryType actual = ParallelData.getCommonLibraryType(containers);
		assertEquals(expected, actual);
	}
	
	static Stream<Arguments> testGetCommonLibraryType() {
		final String c = "1:1-2:.";
		return Stream.of(
				// 1 libraryType
				Arguments.of(
						createDataContainers(
								Collections.nCopies(1, c),
								Arrays.asList("UNSTRANDED")),
						LibraryType.UNSTRANDED),
				Arguments.of(
						createDataContainers(
								Collections.nCopies(1, c),
								Arrays.asList("RF_FIRSTSTRAND")),
						LibraryType.RF_FIRSTSTRAND),
				Arguments.of(
						createDataContainers(
								Collections.nCopies(1, c),
								Arrays.asList("FR_SECONDSTRAND")),
						LibraryType.FR_SECONDSTRAND),
				// 2 identical libraryTypes
				Arguments.of(
						createDataContainers(
								Collections.nCopies(2, c),
								Arrays.asList("UNSTRANDED", "UNSTRANDED")),
						LibraryType.UNSTRANDED),
				Arguments.of(
						createDataContainers(
								Collections.nCopies(2, c),
								Arrays.asList("RF_FIRSTSTRAND", "RF_FIRSTSTRAND")),
						LibraryType.RF_FIRSTSTRAND),
				Arguments.of(
						createDataContainers(
								Collections.nCopies(2, c),
								Arrays.asList("FR_SECONDSTRAND", "FR_SECONDSTRAND")),
						LibraryType.FR_SECONDSTRAND),				
				Arguments.of(
						createDataContainers(
								Collections.nCopies(2, c),
								Arrays.asList("UNSTRANDED", "RF_FIRSTSTRAND")),
						LibraryType.MIXED),
				Arguments.of(
						createDataContainers(
								Collections.nCopies(2, c),
								Arrays.asList("RF_FIRSTSTRAND", "FR_SECONDSTRAND")),
						LibraryType.MIXED),
				Arguments.of(
						createDataContainers(
								Collections.nCopies(2, c),
								Arrays.asList("FR_SECONDSTRAND", "UNSTRANDED")),
						LibraryType.MIXED)				
				);
	}

}