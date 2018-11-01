package test.lib.data.validator.paralleldata;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import lib.data.ParallelData;
import lib.data.validator.paralleldata.ParallelDataValidator;

@TestInstance(Lifecycle.PER_CLASS)
public abstract class AbstractParallelDataValidatorTest {

	private ParallelDataValidator testInstance;

	/*
	 * Test
	 */
	
	@ParameterizedTest(name = "For ParallelData {0} isValid should be {1}")
	@MethodSource("testIsValid")
	void testIsValid(ParallelData parallelData, boolean expected) {
		final boolean actual = testInstance.isValid(parallelData);
		assertEquals(expected, actual);
	}
	
	public void setTestInstance(final ParallelDataValidator testInstance) {
		this.testInstance = testInstance; 
	}
	
	/*
	 * Abstract
	 */

	abstract Stream<Arguments> testIsValid();
	
}
