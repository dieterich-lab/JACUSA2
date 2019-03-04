package test.lib.data.storage.arrest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import lib.data.storage.arrest.LocationInterpreter;
import lib.recordextended.SAMRecordExtended;
import lib.util.coordinate.CoordinateTranslator;
import lib.util.position.Position;
import lib.util.position.PositionProvider;
import lib.util.position.UnmodifiablePosition;

@TestInstance(Lifecycle.PER_CLASS)
interface LocationInterpreterTest {

	@ParameterizedTest(name = "{3}")
	@MethodSource("testGetThroughPositionProvider")
	default void testGetThroughPositionProvider(
			SAMRecordExtended recordExtended,
			CoordinateTranslator translator,
			List<Position> expected,
			String info) {

		final LocationInterpreter testInstance 	= createTestInstance();
		final PositionProvider throughPositionProvider = 
				testInstance.getThroughPositionProvider(recordExtended, translator);
	
		final List<Position> actual = new ArrayList<>(expected.size());
		while (throughPositionProvider.hasNext())
			actual.add(throughPositionProvider.next());
		assertEquals(expected, actual);
	}
	
	@ParameterizedTest(name = "{3}")
	@MethodSource("testGetArrestPosition")
	default void testGetArrestPosition(
			SAMRecordExtended recordExtended,
			CoordinateTranslator translator,
			Position expected,
			String info) {
		
		final LocationInterpreter testInstance 	= createTestInstance();
		final Position actual = testInstance.getArrestPosition(recordExtended, translator);
		assertEquals(expected, actual);
	}
	
	Stream<Arguments> testGetThroughPositionProvider();
	Stream<Arguments> testGetArrestPosition();
	
	LocationInterpreter createTestInstance();

	// ',' separated array of strings of the following form: "ref,winPos,read,{A|C|G|T}" 
	default List<Position> parseExpected(final SAMRecordExtended recordExtended, final String[] str) {
		final List<Position> expected = new ArrayList<Position>(str.length);
		for (final String tmpStr : str) {
			final String[] cols = tmpStr.split(",");
			final int refPos	= Integer.parseInt(cols[0]);
			final int readPos 	= Integer.parseInt(cols[1]);
			final int winPos 	= Integer.parseInt(cols[2]);
			expected.add(new UnmodifiablePosition(refPos, readPos, winPos, recordExtended));
		}
		return expected;
	}
	
}
