package test.jacusa.filter.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.stream.Stream;

import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import jacusa.filter.factory.HomozygousFilterFactory;
import test.utlis.CLIUtils;

/**
 * Tests jacusa.filter.factory.HomozygousFilterFactory
 */
@DisplayName("Test CLI parser of HomozygousFilterFactory")
@TestInstance(Lifecycle.PER_CLASS)
class HomozygousFilterFactoryTest {
	
	private final String LONG_OPT;
	
	public HomozygousFilterFactoryTest() {
		LONG_OPT = HomozygousFilterFactory.getConditionOptionBuilder().build().getLongOpt();
	}
	
	@DisplayName("Test processCLI sets homozygousConditionIndex correctly")
	@ParameterizedTest(name = "Parse line: {1}")
	@MethodSource("testProcessCLI")
	void testProcessCLI(int conditionSize, String line, int expected) throws ParseException {
		final HomozygousFilterFactory testInstance = new HomozygousFilterFactory(conditionSize, null);
		testInstance.processCLI(line);
		final int actual = testInstance.getHomozygousConditionIndex();
		assertEquals(expected, actual);
	}
	
	@DisplayName("Test processCLI fails on wrong input")
	@Test
	void testProcessCLIFails() {
		final int conditionSize = 3;
		final HomozygousFilterFactory testInstance = 
				new HomozygousFilterFactory(conditionSize, null);
		
		// no input
		assertThrows(MissingOptionException.class,
				() -> {
					final String line = new String();
					testInstance.processCLI(line);								
				});
		
		// < 1
		assertThrows(IllegalArgumentException.class,
				() -> {
					final String line = createLine(0);
					testInstance.processCLI(line);								
				});
		// > conditionSize
		assertThrows(IllegalArgumentException.class,
				() -> {
					final String line = createLine(conditionSize + 1);
					testInstance.processCLI(line);								
				});		
		
		// not a number
		assertThrows(IllegalArgumentException.class,
				() -> {
					final String line = createLine("wrong");
					testInstance.processCLI(line);
				});
	}
	
	Stream<Arguments> testProcessCLI() {
		return Stream.of(
				createArguments(2, 1),
				createArguments(2, 2),
				createArguments(4, 3) );
	}
	
	protected Arguments createArguments(final int conditionSize, final int homozygousIndex) {
		return Arguments.of(conditionSize, createLine(homozygousIndex), homozygousIndex - 1);
	}
	
	// TODO move somewhere
	protected String createLine(final String value) {
		return CLIUtils.pr(LONG_OPT, value);
	}

	// TODO move somewhere
	protected String createLine(final int value) {
		return createLine(Integer.toString(value));
	}
	
}
