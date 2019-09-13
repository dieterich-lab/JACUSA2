package test.jacusa.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import jacusa.filter.Filter;
import lib.data.ParallelData;

/**
 * Tests in @see jacusa.filter.AbstractFilter#applyFilter(lib.data.result.Result)
 */

@TestInstance(Lifecycle.PER_CLASS)
abstract class AbstractFilterTest {

	@ParameterizedTest(name = "{3}")
	@MethodSource("testFilter")
	void testFilter(Filter testInstance, ParallelData parallelData, boolean expected, String info) {
		final boolean actual = testInstance.filter(parallelData);
		assertEquals(expected, actual);
	}

	abstract Stream<Arguments> testFilter();

}
