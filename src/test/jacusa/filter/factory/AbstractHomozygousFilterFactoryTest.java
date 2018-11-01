package test.jacusa.filter.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
import jacusa.filter.factory.HomozygousFilterFactory;
import test.utlis.CLIUtils;

@DisplayName("Test CLI parser of HomozygousFilterFactory")
@TestInstance(Lifecycle.PER_CLASS)
public abstract class AbstractHomozygousFilterFactoryTest {
	
	protected static final String LONG_OPT = 
			HomozygousFilterFactory.getConditionOptionBuilder().build().getLongOpt();

	/*
	 * Test
	 */
	
	@DisplayName("Test processCLI sets homozygousConditionIndex correctly")
	@ParameterizedTest(name = "Parse line: {0} and expect HomozygousConditionIndex to be: {1}")
	@MethodSource("testProcessCLI")
	void testProcessCLI(int conditionSize, String line, int expected) throws MissingOptionException {
		createTestInstance(conditionSize);
		getTestInstance().processCLI(line);
		final int actual = getHomozygousConditionIndex();
		assertEquals(expected, actual);
	}
	
	@DisplayName("Test processCLI fails on wrong input")
	@Test
	void testProcessCLIFails() {
		final int conditionSize = 3;
		createTestInstance(3);
		// < 1
		assertThrows(IllegalArgumentException.class,
				() -> {
					final String line = setHomozygousConditionIndex(0);
					getTestInstance().processCLI(line);								
				});
		// > conditionSize
		assertThrows(IllegalArgumentException.class,
				() -> {
					final String line = setHomozygousConditionIndex(conditionSize + 1);
					getTestInstance().processCLI(line);								
				});		
		
		// not a number
		assertThrows(IllegalArgumentException.class,
				() -> {
					final String line = setHomozygousConditionIndex("wrong");
					getTestInstance().processCLI(line);
				});
	}

	/*
	 * Method Source
	 */
	
	public Stream<Arguments> testProcessCLI() {
		return Stream.of(
				Arguments.of(2, setHomozygousConditionIndex(1), 1),
				Arguments.of(2, setHomozygousConditionIndex(2), 2),
				Arguments.of(4, setHomozygousConditionIndex(3), 3) );
	}
	
	/*
	 * Abstract
	 */

	protected String setHomozygousConditionIndex(final int value) {
		return setHomozygousConditionIndex(Integer.toString(value));
	}
	
	private String setHomozygousConditionIndex(final String value) {
		return CLIUtils.pr(LONG_OPT, value);
	}
	
	protected abstract void createTestInstance(final int conditionSize);
	protected abstract AbstractFilterFactory getTestInstance();
	protected abstract int getHomozygousConditionIndex();
	
}
