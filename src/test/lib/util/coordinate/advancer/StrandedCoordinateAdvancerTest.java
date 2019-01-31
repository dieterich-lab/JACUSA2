package test.lib.util.coordinate.advancer;

import java.util.stream.Stream;

import org.junit.jupiter.params.provider.Arguments;

import lib.util.coordinate.Coordinate;
import lib.util.coordinate.CoordinateUtil.STRAND;
import lib.util.coordinate.advancer.CoordinateAdvancer;
import lib.util.coordinate.advancer.StrandedCoordinateAdvancer;
import lib.util.coordinate.OneCoordinate;

class StrandedCoordinateAdvancerTest extends AbstractCoordinateAdvancerTest {

	public StrandedCoordinateAdvancerTest() {
		super(new OneCoordinate.Parser());
	}

	@Override
	protected Stream<Arguments> testAdvance() {
		return Stream.of(
				Arguments.of(
						c("1", 10, 10, STRAND.FORWARD), 5, c("1", 12, 12, STRAND.REVERSE)),
				Arguments.of(
						c("1", 10, 10, STRAND.FORWARD), 1, c("1", 10, 10, STRAND.REVERSE)),
				Arguments.of(
						c("1", 10, 10, STRAND.REVERSE), 1, c("1", 11, 11, STRAND.FORWARD)),
				Arguments.of(
						c("1", 10, 10, STRAND.FORWARD), 2, c("1", 11, 11, STRAND.FORWARD)) );
	}
	
	@Override
	protected CoordinateAdvancer createTestInstance(Coordinate coordinate) {
		return new StrandedCoordinateAdvancer(coordinate);
	}
	
}
