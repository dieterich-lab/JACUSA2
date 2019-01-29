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

import jacusa.filter.factory.MaxAlleleCountFilterFactory;
import test.utlis.CLIUtils;

/**
 * Tests jacusa.filter.factory.MaxAlleleCountFilterFactory
 */
@TestInstance(Lifecycle.PER_CLASS)
public class MaxAlleleCountFilterFactoryTest {

	private final String LONG_OPT;
	
	private MaxAlleleCountFilterFactory testInstance;
	
	public MaxAlleleCountFilterFactoryTest() {
		LONG_OPT = MaxAlleleCountFilterFactory.getMaxAllelesOptionBuilder().build().getLongOpt();
	}

	@BeforeEach
	void beforeEach() {
		testInstance = new MaxAlleleCountFilterFactory(null); 
	}
	
	@DisplayName("Test processCLI sets maxAlleles correctly")
	@ParameterizedTest(name = "Process line: {0}")
	@MethodSource("testProcessCLI")
	void testProcessCLI(String line, int expected) throws ParseException {
		testInstance.processCLI(line);
		final int actual = testInstance.getMaxAlleles();
		assertEquals(expected, actual);
	}
	
	@DisplayName("Test processCLI fails on wrong input")
	@Test
	void testProcessCLIFails() {
		// < 1
		assertThrows(IllegalArgumentException.class,
				() -> {
					final String line = createLine(0);
					testInstance.processCLI(line);								
				});
		// not a number
		assertThrows(IllegalArgumentException.class,
				() -> {
					final String line = createLine("wrong");
					testInstance.processCLI(line);
				});
	}

	public Stream<Arguments> testProcessCLI() {
		return IntStream.rangeClosed(1,  3)
			.mapToObj(i -> Arguments.of(createLine(i), i));
	}

	// TODO move somewhere
	private String createLine(int value) {
		return createLine(Integer.toString(value));
	}
	
	// TODO move somewhere
	private String createLine(String value) {
		return CLIUtils.pr(LONG_OPT, value);
	}

}
