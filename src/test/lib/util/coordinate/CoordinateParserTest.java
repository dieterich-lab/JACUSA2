package test.lib.util.coordinate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import lib.util.coordinate.Coordinate;
import lib.util.coordinate.CoordinateUtil.STRAND;

// JUNIT: A 
public class CoordinateParserTest {

	private Coordinate.Parser testInstance;
	
	@BeforeEach
	void beforeEach() {
		testInstance = new Coordinate.Parser();
	}
	
	@ParameterizedTest(name = "Parse String {0} and expect Coord.: {1}")
	@MethodSource("testParse")
	public void testParse(String s, Coordinate expected) {
		final Coordinate actual = testInstance.parse(s);
		assertEquals(expected, actual);
	}
	
	/*
	 * Format:
	 * 1.	String(contig:start-end:strand)
	 * 2.	Coordinate
	 */
	static Stream<Arguments> testParse() {
		return Stream.of(
				Arguments.of(
						"1:1-2:.", 
						new Coordinate("1", 1, 2, STRAND.UNKNOWN)),
				Arguments.of(
						"test:100-200:+", 
						new Coordinate("test", 100, 200, STRAND.FORWARD)),
				Arguments.of(
						"test:100-200:-", 
						new Coordinate("test", 100, 200, STRAND.REVERSE)) );
	}

	@ParameterizedTest(name = "Wrap Coord. {0} and expect String: {1}")
	@MethodSource("testWrap")
	public void testWrap(Coordinate coordinate, String expected) {
		final String actual = testInstance.wrap(coordinate);
		assertEquals(expected, actual);
	}

	/*
	 * Format:
	 * 1.	Coordinate
	 * 2.	String(contig:start-end:strand)
	 * 
	 */
	static Stream<Arguments> testWrap() {
		return Stream.of(
				Arguments.of(
						new Coordinate("1", 1, 2, STRAND.UNKNOWN), 
						"1:1-2:."),
				Arguments.of(
						new Coordinate("test", 100, 200, STRAND.FORWARD), 
						"test:100-200:+"),
				Arguments.of(
						new Coordinate("test", 100, 200, STRAND.REVERSE), 
						"test:100-200:-") );
	}
	
	@Test
	void testParseFails() {
		final String[] wrongInput = new String[] {
				"test", "test:1:.", "test:1-2", "test:1-2:", "test:1-2:*" };
		for (final String s : wrongInput) {
			final Executable executable = () -> { testInstance.parse(s); };
			assertThrows(IllegalArgumentException.class, executable);
		}
	}

}