package test.lib.location;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import lib.location.StrandedCoordinateAdvancer;
import lib.util.coordinate.Coordinate;
import lib.util.coordinate.CoordinateUtil.STRAND;

class StrandedCoordinateAdvancerTest {

	private StrandedCoordinateAdvancer strandedCoordinateAdvancer; 
		
	/*
	 * Tests
	 */
	
	@DisplayName("Test advance gives correct results")
	@ParameterizedTest(name = "Advance {0} {1}x and expect {1}")
	@MethodSource("testAdvance")
	void testAdvance(Coordinate coordinate, int advance, Coordinate expected) {
		strandedCoordinateAdvancer = new StrandedCoordinateAdvancer(coordinate);
		for (int i = 0; i < advance; ++i) {
			strandedCoordinateAdvancer.advance();
		}
		final Coordinate actual = strandedCoordinateAdvancer.getCurrentCoordinate();
		assertEquals(expected, actual);
	}

	/*
	 * Method Source
	 */
	
	static Stream<Arguments> testAdvance() {
		return Stream.of(
				Arguments.of(new Coordinate("1", 10, 11, STRAND.FORWARD), 5, new Coordinate("1", 12, 13, STRAND.REVERSE)),
				Arguments.of(new Coordinate("1", 10, 11, STRAND.FORWARD), 1, new Coordinate("1", 10, 11, STRAND.REVERSE)),
				Arguments.of(new Coordinate("1", 10, 11, STRAND.REVERSE), 1, new Coordinate("1", 11, 12, STRAND.FORWARD)),
				Arguments.of(new Coordinate("1", 10, 11, STRAND.FORWARD), 2, new Coordinate("1", 11, 12, STRAND.FORWARD)) );
	}
	
}
