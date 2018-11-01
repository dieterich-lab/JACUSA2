package test.lib.location;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import lib.location.UnstrandedCoordinateAdvancer;
import lib.util.coordinate.Coordinate;
import lib.util.coordinate.CoordinateUtil.STRAND;

class UnstrandedCoordinateAdvancerTest {
	
	private UnstrandedCoordinateAdvancer unstrandedCoordinateAdvancer; 
	
	/*
	 * Tests
	 */
	
	@DisplayName("Test advance gives correct results")
	@ParameterizedTest(name = "Advance {0} {1}x and expect {2}")
	@MethodSource("testAdvance")
	void testAdvance(Coordinate coordinate, int advance, Coordinate expected) {
		unstrandedCoordinateAdvancer = new UnstrandedCoordinateAdvancer(coordinate);
		for (int i = 0; i < advance; ++i) {
			unstrandedCoordinateAdvancer.advance();
		}
		final Coordinate actual = unstrandedCoordinateAdvancer.getCurrentCoordinate();
		assertEquals(expected, actual);
	}

	/*
	 * Method Source
	 */
	
	static Stream<Arguments> testAdvance() {
		return Stream.of(
				Arguments.of(new Coordinate("1", 10, 11), 5, new Coordinate("1", 15, 16)),
				Arguments.of(new Coordinate("1", 10, 11, STRAND.REVERSE), 1, new Coordinate("1", 11, 12, STRAND.REVERSE)),
				Arguments.of(new Coordinate("1", 10, 11, STRAND.REVERSE), 1, new Coordinate("1", 11, 12, STRAND.REVERSE)),
				Arguments.of(new Coordinate("1", 10, 11, STRAND.FORWARD), 2, new Coordinate("1", 12, 13, STRAND.FORWARD)) );
	}

}
