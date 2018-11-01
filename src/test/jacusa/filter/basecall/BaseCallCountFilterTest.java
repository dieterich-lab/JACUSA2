package test.jacusa.filter.basecall;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import jacusa.filter.FilterByRatio;
import jacusa.filter.basecall.GenericBaseCallCountFilter;
import lib.data.DataType;
import lib.data.cache.fetcher.DataTypeFetcher;
import lib.data.cache.fetcher.Fetcher;
import lib.data.count.basecall.ArrayBaseCallCount;
import lib.data.count.basecall.BaseCallCount;
import lib.data.result.Result;

@TestInstance(Lifecycle.PER_METHOD)
public class BaseCallCountFilterTest {

	private final BaseCallCount.AbstractParser bccParser;
	
	public BaseCallCountFilterTest() {
		bccParser = new ArrayBaseCallCount.Parser();
	}
	
	private static GenericBaseCallCountFilter BASE_CALL_COUNT_FILTER;
	
	@BeforeAll
	static void setupBaseCallCountFilter() {
		final Fetcher<BaseCallCount> observed = new DataTypeFetcher<>(DataType.create("Filtered", BaseCallCount.class));
		final Fetcher<BaseCallCount> filtered = new DataTypeFetcher<>(DataType.create("Observed", BaseCallCount.class));
		final FilterByRatio filterByRatio = new FilterByRatio(0.5);
		BASE_CALL_COUNT_FILTER = new GenericBaseCallCountFilter('X', observed, filtered, 0, filterByRatio);
	}

	/*
	 * Tests
	 */

	/**
	 * Test method for {@link jacusa.filter.basecall.GenericBaseCallCountFilter#applyFilter(lib.data.result.Result)}.
	 */
	@DisplayName("Should filter when appropiate")
	@ParameterizedTest(name = "Result: {0}")
	@MethodSource("testApplyFilter")
	void testApplyFilter(Result result, boolean expected) {
		boolean actual = BASE_CALL_COUNT_FILTER.applyFilter(result);
		assertEquals(expected, actual);
	}

	/*
	 * Method Source
	 */
	
	Stream<Arguments> testCreateBaseCallCount() {
		final BaseCallCount[] baseCallCounts1 = Stream.of("4,3,2,1")
				.map(s -> bccParser.parse(s))
				.toArray(BaseCallCount[]::new);
		final BaseCallCount[] baseCallCounts2 = Stream.of("1,2,3,3", "2,3,4,50")
				.map(s -> bccParser.parse(s))
				.toArray(BaseCallCount[]::new);
		final BaseCallCount[] baseCallCounts3 = Stream.of("1,2,3,4", "10,10,10,10", "0,0,0,0")
				.map(s -> bccParser.parse(s))
				.toArray(BaseCallCount[]::new);
		final BaseCallCount[] baseCallCounts4 = Stream.of("4,4,4,4", "5,5,5,5", "3,3,3,3", "6,7,8,9")
				.map(s -> bccParser.parse(s))
				.toArray(BaseCallCount[]::new);
		return Stream.of(baseCallCounts1, baseCallCounts2, baseCallCounts3, baseCallCounts4)
			.map(Arguments::of);
			
	}
}
