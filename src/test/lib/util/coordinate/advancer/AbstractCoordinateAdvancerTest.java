package test.lib.util.coordinate.advancer;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import lib.util.coordinate.Coordinate;
import lib.util.coordinate.CoordinateUtil.STRAND;
import lib.util.coordinate.advancer.CoordinateAdvancer;
import test.lib.util.coordinate.OneCoordinateArgumentConverter;

@TestInstance(Lifecycle.PER_CLASS)
abstract class AbstractCoordinateAdvancerTest {
	
	private final Coordinate.AbstractParser parser;
	
	public AbstractCoordinateAdvancerTest(final Coordinate.AbstractParser parser) {
		this.parser = parser;
	}
	
	@ParameterizedTest(name = "Advance {0} {1}x")
	@MethodSource("testAdvance")
	void testAdvance(
			@ConvertWith(OneCoordinateArgumentConverter.class) Coordinate coordinate, 
			int advance, 
			@ConvertWith(OneCoordinateArgumentConverter.class) Coordinate expected) {
		
		final CoordinateAdvancer testInstance = createTestInstance(coordinate);
		for (int i = 0; i < advance; ++i) {
			testInstance.advance();
		}
		final Coordinate actual = testInstance.getCurrentCoordinate();
		assertEquals(expected, actual);
	}
	
	/*
	 * Helper: to set separating string correctly, e.g.: 1:10-12:+
	 */
	protected String c(final String contig, final int start, final int end, final STRAND strand) {
		final Coordinate coordinate = parser.create(contig, start, end, strand);
		return parser.wrap(coordinate);
	}
	
	protected abstract CoordinateAdvancer createTestInstance(Coordinate coordinate);
	
	/*
	 * Format:
	 * 1. Coordinate(current)
	 * 2. int(steps to advance)
	 * 3. Coordinate(expected)
	 */
	protected abstract Stream<Arguments> testAdvance();
	
}
