package test.lib.location;

import java.util.stream.Stream;

import org.junit.jupiter.params.provider.Arguments;

import lib.location.CoordinateAdvancer;
import lib.location.StrandedCoordinateAdvancer;
import lib.util.coordinate.Coordinate;
import lib.util.coordinate.CoordinateUtil.STRAND;
import lib.util.coordinate.ZeroCoordinate;

class StrandedCoordinateAdvancerTest extends AbstractCoordinateAdvancerTest {

	public StrandedCoordinateAdvancerTest() {
		super(new ZeroCoordinate.Parser());
	}

	@Override
	protected Stream<Arguments> testAdvance() {
		return Stream.of(
				Arguments.of(
						c("1", 10, 11, STRAND.FORWARD), 5, c("1", 12, 13, STRAND.REVERSE)),
				Arguments.of(
						c("1", 10, 11, STRAND.FORWARD), 1, c("1", 10, 11, STRAND.REVERSE)),
				Arguments.of(
						c("1", 10, 11, STRAND.REVERSE), 1, c("1", 11, 12, STRAND.FORWARD)),
				Arguments.of(
						c("1", 10, 11, STRAND.FORWARD), 2, c("1", 11, 12, STRAND.FORWARD)) );
	}
	
	@Override
	protected CoordinateAdvancer createTestInstance(Coordinate coordinate) {
		return new StrandedCoordinateAdvancer(coordinate);
	}
	
}
