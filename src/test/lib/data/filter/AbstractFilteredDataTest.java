package test.lib.data.filter;

import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import lib.data.Data;
import lib.data.filter.AbstractFilteredData;
import lib.data.filter.FilteredDataContainer;

@TestInstance(Lifecycle.PER_CLASS)
public abstract class AbstractFilteredDataTest<F extends FilteredDataContainer<F, T>, T extends Data<T>> {

	protected final AbstractFilteredData.AbstractParser<F, T> parser;
	
	public AbstractFilteredDataTest(final AbstractFilteredData.AbstractParser<F, T> parser) {
		this.parser = parser;
	}
	
	// A, B, C used filters
	// use rest to test
	
	/*
	 * Test
	 */

	@Test
	abstract void testParserParseFail();
	
	@ParameterizedTest(name = "Parse String {0} and expect data: {1}")
	@MethodSource("testParserParse")
	void testParserParse(String s, F expected) {
		final F actual = parser.parse(s);
		assertEquals(expected, actual);
	}
	
	@ParameterizedTest(name = "Wrap data {0} and expect String: {1}")
	@MethodSource("testParserWrap")
	void testParserWrap(FilteredDataContainer<F, T> data, String expected) {
		final String actual = parser.wrap(data);
		assertEquals(expected, actual);
	}
	
	@DisplayName("Test merge")
	@ParameterizedTest()
	void testMerge(F data1, F data2, F expected) {
		data1.merge(data2);
		// check same filters
		myAssertEquals(expected, data1);
	}

	@DisplayName("Test copy")
	@ParameterizedTest()
	void testCopy(F data) {
		final F copy = data.copy();
		myAssertEqualSize(data, copy);
		copy.add('X', data.get('A'));
		myAssertNotEqualSize(data, copy);
	}

	/*
	 * Helper
	 */

	protected abstract boolean myEquals(T data1, T data2);

	public void myAssertEquals(F expected, F actual) {
		final Set<Character> filters = new HashSet<>(expected.getFilters());
		filters.addAll(actual.getFilters());
		for (final char c : filters) {
			assertTrue(myEquals(expected.get(c), actual.get(c)), "Mismatch for filter: " + c);
		}
	}
	
	protected void myAssertNotEqualSize(F data1, F data2) {
		final int size1 = data1.getFilters().size();
		final int size2 = data2.getFilters().size();
		assertNotEquals(size1, size2);
	}

	protected void myAssertEqualSize(F data1, F data2) {
		final int size1 = data1.getFilters().size();
		final int size2 = data2.getFilters().size();
		assertEquals(size1, size2);
	}
	
	// method source
	abstract Stream<Arguments> testParserParse();
	abstract Stream<Arguments> testParserWrap();

}