package test.lib.util.coordinate;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import lib.util.coordinate.Coordinate;
import lib.util.coordinate.CoordinateUtil;

@DisplayName("Test CoordinateUtil")
public class CoordinateUtilTest {

	/*
	 * Tests
	 */
	
	@DisplayName("Check isContained produces correct results")
	@ParameterizedTest(name = "Position {1} is contained within {0} : {2}")
	@MethodSource("testIsContained")
	void testIsContained(Coordinate coordinate, int position, boolean expected) {
		final boolean actual =  CoordinateUtil.isContained(coordinate, position);
		assertEquals(expected, actual);
	}

	@DisplayName("Test makeRelativePosition")
	@ParameterizedTest(name = "For position {1} in {0} the relative position is {2}")
	@MethodSource("testMakeRelativePosition")
	void testMakeRelativePosition(Coordinate coordinate, int position, int expected) {
		final int actual = CoordinateUtil.makeRelativePosition(coordinate, position);
		assertEquals(expected, actual);
	}

	/*
	 * Method Source
	 */
	
	static Stream<Arguments> testIsContained() {
		return Stream.of(
				Arguments.of(new Coordinate("1", 5, 10), 5, true),
				Arguments.of(new Coordinate("1", 5, 10), 7, true),
				Arguments.of(new Coordinate("1", 5, 10), 10, true),
				Arguments.of(new Coordinate("1", 5, 10), 4, false),
				Arguments.of(new Coordinate("1", 5, 10), 11, false) );
	}

	static Stream<Arguments> testMakeRelativePosition() {
		return Stream.of(
				Arguments.of(new Coordinate("1", 20, 30), 20, 0),
				Arguments.of(new Coordinate("1", 20, 30), 30, 10),
				Arguments.of(new Coordinate("1", 20, 30), 19, -1),
				Arguments.of(new Coordinate("1", 20, 30), 31, -1) );
	}

	public static class ToCoordinateArgumentConverter extends SimpleArgumentConverter {

		@Override
		protected Object convert(Object src, Class<?> target) throws ArgumentConversionException {
			assertEquals(Coordinate.class, target, "Can only convert to Coordinate");
			final String s = String.valueOf(src);
			return new Coordinate.Parser()
					.parse(s);
		}

	}
	
}
