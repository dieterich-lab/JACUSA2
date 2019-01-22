package test.jacusa.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import jacusa.filter.FilterByRatio;

/**
 * Tests @see test.jacusa.filter.FilterByRatio#filter(int, int)
 */
class FilterByRatioTest {

	@DisplayName("Should filter when appropriate")
	@ParameterizedTest(name = "Given the minRatio {0} and count {1} and filteredCount {2} it should be filtered: {3}")
	@CsvSource( { 
		"0.5,10,5,true",
		"0.5,10,6,false" } )
	void testFilter(double minRatio, int count, int filteredCount, boolean expected) {
		final FilterByRatio filterByRatio = new FilterByRatio(minRatio);
		final boolean actual = filterByRatio.filter(count, filteredCount);
		assertEquals(expected, actual);
	}

}