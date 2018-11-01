package test.jacusa.filter.factory;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.cli.MissingOptionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import jacusa.filter.factory.HomopolymerFilterFactory;
import test.utlis.CLIUtils;

@DisplayName("Test CLI parser of HomopolymerFilterFactory")
class HomopolymerFilterFactoryTest {

	private static final String LONG_OPT = 
			HomopolymerFilterFactory.getHomopolymerOptionBuilder().build().getLongOpt();
	
	private HomopolymerFilterFactory testInstance;
	
	@BeforeEach
	void beforeEach() {
		testInstance = new HomopolymerFilterFactory(null); 
	}

	/*
	 * Test
	 */

	@DisplayName("Test processCLI length")
	@ParameterizedTest(name = "Process line: {0} and expected {1}")
	@MethodSource("testProcessCLI")
	void testProcessCLI(String line, int expected) throws MissingOptionException {
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

	/*
	 * Method Source
	 */
	
	public static Stream<Arguments> testProcessCLI() {
		return IntStream.rangeClosed(1,  3)
			.mapToObj(i -> Arguments.of(CLIUtils.pr(LONG_OPT, Integer.toString(i)), i));
	}
	
}
