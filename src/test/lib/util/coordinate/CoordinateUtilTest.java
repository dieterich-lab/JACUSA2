package test.lib.util.coordinate;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.CsvSource;

import lib.util.coordinate.Coordinate;
import lib.util.coordinate.CoordinateUtil;

// JUNIT: A
class CoordinateUtilTest {

	@ParameterizedTest(name = "Position {1} is contained within {0} : {2}")
	@CsvSource(
			delimiter = '\t',
			value = {
					"1:5-10:.	5	true",
					"1:5-10:.	7	true",
					"1:5-10:.	10	true",
					"1:5-10:.	4	false",
					"1:5-10:.	11	false"
			})
	void testIsContained(
			@ConvertWith(CoordinateArgumentConverter.class) Coordinate coordinate, 
			int position, 
			boolean expected) {
		final boolean actual =  CoordinateUtil.isContained(coordinate, position);
		assertEquals(expected, actual);
	}

	@ParameterizedTest(name = "For position {1} in {0} the relative position is {2}")
	@CsvSource(
			delimiter = '\t',
			value = {
					"1:20-30:.	20	0",
					"1:20-30:.	30	10",
					"1:20-30:.	19	-1",
					"1:20-30:.	31	-1"
			})
	void testMakeRelativePosition(
			@ConvertWith(CoordinateArgumentConverter.class) Coordinate coordinate,
			int position, 
			int expected) {

		final int actual = CoordinateUtil.makeRelativePosition(coordinate, position);
		assertEquals(expected, actual);
	}
	
}