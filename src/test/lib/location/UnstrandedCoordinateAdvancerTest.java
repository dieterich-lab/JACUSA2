package test.lib.location;

import java.util.stream.Stream;

import org.junit.jupiter.params.provider.Arguments;

import lib.location.CoordinateAdvancer;
import lib.location.UnstrandedCoordinateAdvancer;
import lib.util.coordinate.Coordinate;
import lib.util.coordinate.CoordinateUtil.STRAND;

class UnstrandedCoordinateAdvancerTest extends AbstractCoordinateAdvancerTest {

	@Override
	protected Stream<Arguments> testAdvance() {
		return Stream.of(
				Arguments.of(
						c("1", 10, 11, STRAND.UNKNOWN), 5, c("1", 15, 16, STRAND.UNKNOWN)),
				Arguments.of(
						c("1", 10, 11, STRAND.REVERSE), 1, c("1", 11, 12, STRAND.REVERSE)),
				Arguments.of(
						c("1", 10, 11, STRAND.REVERSE), 1, c("1", 11, 12, STRAND.REVERSE)),
				Arguments.of(
						c("1", 10, 11, STRAND.FORWARD), 2, c("1", 12, 13, STRAND.FORWARD)) );
	}

	@Override
	protected CoordinateAdvancer createTestInstance(Coordinate coordinate) {
		return new UnstrandedCoordinateAdvancer(coordinate);
	}
	
}
