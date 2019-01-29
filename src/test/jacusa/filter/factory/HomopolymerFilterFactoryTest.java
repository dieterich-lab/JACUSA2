package test.jacusa.filter.factory;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import jacusa.filter.factory.HomopolymerFilterFactory;
import test.utlis.CLIUtils;

/**
 * Tests @see test.jacusa.filter.factory.HomopolymerFilterFactory
 */
@DisplayName("Test CLI parser of HomopolymerFilterFactory")
@TestInstance(Lifecycle.PER_CLASS)
class HomopolymerFilterFactoryTest {

	private final String LONG_OPT;

	private HomopolymerFilterFactory testInstance;
	
	public HomopolymerFilterFactoryTest() {
		LONG_OPT = HomopolymerFilterFactory.getHomopolymerOptionBuilder().build().getLongOpt();
	}
	
	@BeforeEach
	void beforeEach() {
		testInstance = new HomopolymerFilterFactory(null); 
	}

	@DisplayName("Test processCLI length")
	@ParameterizedTest(name = "Process line: {0}")
	@MethodSource("testProcessCLI")
	void testProcessCLI(String line, int expected) throws ParseException {
		testInstance.processCLI(line);
		final int actual = testInstance.getLength();
		assertEquals(expected, actual);
	}
	
	@DisplayName("Check processCLI fails on wrong input")
	@Test
	void testProcessCLIFails() {
		// < 1
		assertThrows(IllegalArgumentException.class,
				() -> {
					final String line = CLIUtils.pr(LONG_OPT, Integer.toString(0));
					testInstance.processCLI(line);								
				});
		// not a number
		assertThrows(IllegalArgumentException.class,
				() -> {
					final String line = CLIUtils.pr(LONG_OPT, "wrong");
					testInstance.processCLI(line);
				});
	}
	
	Stream<Arguments> testProcessCLI() {
		return IntStream.rangeClosed(1,  3)
			.mapToObj(i -> Arguments.of(CLIUtils.pr(LONG_OPT, Integer.toString(i)), i));
	}
	
}
