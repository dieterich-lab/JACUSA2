package test.lib.location;

import java.util.stream.Stream;

import org.junit.jupiter.params.provider.Arguments;

import lib.location.CoordinateAdvancer;
import lib.util.coordinate.Coordinate;

public class StrandedJumpingCoordinateAdvancerTest extends AbstractCoordinateAdvancerTest {

	private static final int CONDITION_SIZE = 1;
	
	@Override
	protected Stream<Arguments> testAdvance() {
		return null;
	}
	
	@Override
	protected CoordinateAdvancer createTestInstance(Coordinate coordinate) {
		return null;
		/*
		final GeneralParameter parameter = new CallParameter(1);
		final ConditionContainer conditionContainer = new ConditionContainer<>(parameter);
		final CoordinateController coordinateController = new CoordinateController(conditionContainer);
		
		return new StrandedJumpingCoordinateAdvancer(coordinateController, conditionContainer);
		*/
	}

}
