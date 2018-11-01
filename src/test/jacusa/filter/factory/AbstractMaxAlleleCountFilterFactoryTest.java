package test.jacusa.filter.factory;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.cli.MissingOptionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import jacusa.filter.factory.AbstractFilterFactory;
import jacusa.filter.factory.MaxAlleleCountFilterFactory;
import test.utlis.CLIUtils;

@TestInstance(Lifecycle.PER_CLASS)
public abstract class AbstractMaxAlleleCountFilterFactoryTest {

	private static final String LONG_OPT = 
			MaxAlleleCountFilterFactory.getMaxAllelesOptionBuilder().build().getLongOpt();

	/*
	 * Test
	 */

	@DisplayName("Test processCLI sets maxAlleles correctly")
	@ParameterizedTest(name = "Process line: {0} and expected {1}")
	@MethodSource("testProcessCLI")
	void testProcessCLI(String line, int expected) throws MissingOptionException {
		getTestInstance().processCLI(line);
		final int actual = getMaxAlleles();
		assertEquals(expected, actual);
	}
	
	@DisplayName("Test processCLI fails on wrong input")
	@Test
	void testProcessCLIFails() {
		// < 1
		assertThrows(IllegalArgumentException.class,
				() -> {
					final String line = setMaxAlleles(0);
					getTestInstance().processCLI(line);								
				});
		// not a number
		assertThrows(IllegalArgumentException.class,
				() -> {
					final String line = setMaxAlleles("wrong");
					getTestInstance().processCLI(line);
				});
	}

	/*
	 * Method Source
	 */
	
	public Stream<Arguments> testProcessCLI() {
		return IntStream.rangeClosed(1,  3)
			.mapToObj(i -> Arguments.of(setMaxAlleles(i), i));
	}
	
	/*
	 * Abstract
	 */

	protected String setMaxAlleles(int value) {
		return setMaxAlleles(Integer.toString(value));
	}
	
	private String setMaxAlleles(String value) {
		return CLIUtils.pr(LONG_OPT, value);
	}

	protected abstract AbstractFilterFactory getTestInstance();
	protected abstract int getMaxAlleles();
	
}
