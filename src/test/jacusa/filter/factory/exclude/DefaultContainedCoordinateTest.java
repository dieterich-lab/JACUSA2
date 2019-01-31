package test.jacusa.filter.factory.exclude;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.CsvSource;

import jacusa.filter.factory.exclude.DefaultContainedCoordinate;
import lib.util.coordinate.Coordinate;
import lib.util.coordinate.OneCoordinate;
import test.lib.util.coordinate.OneCoordinateArgumentConverter;

/**
 * Tests @see jacusa.filter.factory.exclude.DefaultContainedCoordinate#isContained(Coordinate)
 */
@TestInstance(Lifecycle.PER_CLASS)
class DefaultContainedCoordinateTest {

	private DefaultContainedCoordinate testInstance;
	
	@BeforeAll
	void beforeAll() {
		testInstance = createTestInstance();
	}
	
	DefaultContainedCoordinate createTestInstance() {
		final Coordinate.AbstractParser parser = new OneCoordinate.Parser();
		final Map<String, List<Coordinate>> contig2coordinate = Arrays.asList(
				"1:1-100:.", "1:200-300:.",
				"2:1-100:.", "2:200-300:.",
				"3:1-100:.", "3:200-300:.")
				.stream()
				.map(s -> parser.parse(s))
				.collect(Collectors.groupingBy(Coordinate::getContig));
		return new DefaultContainedCoordinate(contig2coordinate);
	}
	
	// TODO throw when coordinate not sorted
	@Disabled
	@Test
	void testIsContainedFails() {
		testInstance = createTestInstance();
		
		testInstance.isContained(new OneCoordinate("1", 2, 2));
		testInstance.isContained(new OneCoordinate("1", 1, 1));
	}
	
	@ParameterizedTest(name = "Test if {0} is contained")
	@CsvSource(
			delimiter = '\t',
			value = {
					"1:1-1:.	true",
					"1:5-5:.	true",
					"1:6-6:.	true",
					"1:150-150:.	false",
					"1:200-200:.	true",
					"3:5-5:.	true",
					"4:5-5:.	false",
					"2:5-5:.	true",
			})
	void testIsContained(
			@ConvertWith(OneCoordinateArgumentConverter.class) Coordinate coordinate,
			boolean expected) {
		
		final boolean actual = testInstance.isContained(coordinate);
		assertEquals(expected, actual);
	}
	
}
