package test.lib.data.count.basecall;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import lib.data.count.basecall.BaseCallCount;
import lib.util.Base;

@TestInstance(Lifecycle.PER_CLASS)
public abstract class AbstractBaseCallCountTest {

	private final BaseCallCount.AbstractParser parser;

	public AbstractBaseCallCountTest(final BaseCallCount.AbstractParser parser) {
		this.parser = parser;
	}
	
	abstract BaseCallCount create();
	
	/**
	 * Test method for {@link lib.data.count.basecall.BaseCallCount.AbstractParser#parse(String)}.
	 */
	@DisplayName("Should parse String and build BaseCallCount")
	@ParameterizedTest(name = "String: {0} should be parsed to {1}")
	@MethodSource("testParserParse")
	void testParserParse(String s, BaseCallCount expected) {
		assertEquals(expected, parser.parse(s));
	}
	
	/**
	 * Test method for {@link lib.data.count.basecall.BaseCallCount.AbstractParser#parse(String)}.
	 */
	@DisplayName("Should fail to parse String")
	@Test
	void testParserParseFail() {
		// not enough fields
		assertThrows(IllegalArgumentException.class, () -> parser.parse("10;0,0"));
		// wrong sep
		assertThrows(IllegalArgumentException.class, () -> parser.parse("10,0,0,0"));
		// not a number
		assertThrows(IllegalArgumentException.class, () -> parser.parse("10;0;0,A"));
		assertThrows(IllegalArgumentException.class, () -> parser.parse("10;0;0;-10"));
	}
	
	/**
	 * Test method for {@link lib.data.count.basecall.BaseCallCount.AbstractParser#wrap(BaseCallCount)}.
	 */
	@DisplayName("Should wrap BaseCallCount")
	@ParameterizedTest(name = "BaseCallCount: {0} should be wrapped to {1}")
	@MethodSource("testParserWrap")
	void testParserWrap(BaseCallCount baseCallCount, String expected) {
		assertEquals(expected, parser.wrap(baseCallCount));
	}

	/**
	 * Test method for {@link lib.data.count.basecall.BaseCallCount#getAlleles()}.
	 */
	@DisplayName("Should calculate the correct alleles")
	@ParameterizedTest(name = "BaseCallCount: {0} should have following alleles {1}")
	@MethodSource("testGetAlleles")
	void testGetAlleles(BaseCallCount baseCallCount, Set<Base> expected) {
		assertEquals(expected, baseCallCount.getAlleles());
	}
	
	/**
	 * Test method for {@link lib.data.count.basecall.BaseCallCount#increment()}.
	 */
	@DisplayName("Should increment correctly")
	@ParameterizedTest(name = "BaseCallCount: {0} should increment {1} giving {2}")
	@MethodSource("testIncrement")
	void testIncrement(BaseCallCount baseCallCount, Base base, BaseCallCount expected) {
		baseCallCount.increment(base);
		myAssertEquals(baseCallCount, expected);
	}
	
	/**
	 * Test method for {@link lib.data.count.basecall.BaseCallCount#clear()}.
	 */
	@DisplayName("Should clear correctly")
	@ParameterizedTest(name = "BaseCallCount: {0} should be empty")
	@MethodSource("testClear")
	void testClear(BaseCallCount baseCallCount) {
		baseCallCount.clear();
		final List<Integer> expected = Collections.nCopies(Base.validValues().length, 0);
		myAssertEquals(expected, baseCallCount);
	}

	/**
	 * Test method for {@link lib.data.count.basecall.BaseCallCount#set()}.
	 */
	@DisplayName("Should set base call count correctly")
	@ParameterizedTest(name = "Given BaseCallCount {0}, set base {1} to {2} resulting in {3}")
	@MethodSource("testSet")
	void testSet(BaseCallCount baseCallCount, Base base, int count, BaseCallCount expected) {
		baseCallCount.set(base, count);
		myAssertEquals(expected, baseCallCount);
	}
	
	/**
	 * Test method for {@link lib.data.count.basecall.BaseCallCount#add(Base, BaseCallCount))}.
	 */
	@DisplayName("Should add base call count correctly")
	@ParameterizedTest(name = "{0} should add base {1} from {2} giving {3} ")
	@MethodSource("testAddBaseBaseCallCount")
	void testAddBaseBaseCallCount(BaseCallCount baseCallCount1, Base base, BaseCallCount baseCallCount2, BaseCallCount expected) {
		baseCallCount1.add(base, baseCallCount2);
		myAssertEquals(expected, baseCallCount1);
	}

	/**
	 * Test method for {@link lib.data.count.basecall.BaseCallCount#add(BaseCallCount)))}.
	 */
	@DisplayName("Should add base call count correctly")
	@ParameterizedTest(name = "Adding {0} and {1} should result in {3} ")
	@MethodSource("testAddBaseCallCount")
	void testAddBaseCallCount(BaseCallCount baseCallCount1, BaseCallCount baseCallCount2, BaseCallCount expected) {
		baseCallCount1.add(baseCallCount2);
		myAssertEquals(expected, baseCallCount1);
	}

	/**
	 * Test method for {@link lib.data.count.basecall.BaseCallCount#add(Base, Base, BaseCallCount)}.
	 */
	@DisplayName("Should add base call count correctly")
	@ParameterizedTest(name = "Add to base {1} in {0} from base {2} in {3} resulting in {3} ")
	@MethodSource("testAddDestSrcBaseCallCount")
	void testAddDestSrcBaseCallCount(BaseCallCount baseCallCount1, Base dest, Base src, BaseCallCount baseCallCount2, BaseCallCount expected) {
		baseCallCount1.add(dest, src, baseCallCount2);
		myAssertEquals(expected, baseCallCount1);
	}

	/**
	 * Test method for {@link lib.data.count.basecall.BaseCallCount#subtract(Base, BaseCallCount))}.
	 */
	@DisplayName("Should subtract base call count correctly")
	@ParameterizedTest(name = "In {0} subtract base {1} from {2} giving {3}")
	@MethodSource("testSubtractBaseBaseCallCount")
	void testSubtractBaseBaseCallCount(BaseCallCount baseCallCount1, Base base, BaseCallCount baseCallCount2, BaseCallCount expected) {
		baseCallCount1.subtract(base, baseCallCount2);
		myAssertEquals(expected, baseCallCount1);
	}

	/**
	 * Test method for {@link lib.data.count.basecall.BaseCallCount#add(BaseCallCount)))}.
	 */
	@DisplayName("Should add base call count correctly")
	@ParameterizedTest(name = "Adding {0} and {1} should result in {3} ")
	@MethodSource("testSubtractBaseCallCount")
	void testSubtractBaseCallCount(BaseCallCount baseCallCount1, BaseCallCount baseCallCount2, BaseCallCount expected) {
		baseCallCount1.subtract(baseCallCount2);
		myAssertEquals(expected, baseCallCount1);
	}

	/**
	 * Test method for {@link lib.data.count.basecall.BaseCallCount#subtract(Base, Baabstractse, BaseCallCount)}.
	 */
	@DisplayName("Should subtract base call count correctly")
	@ParameterizedTest(name = "Subtract Base {1} in BaseCallCount {0} and base {2} in {3} resulting {4} ")
	@MethodSource("testSubtractDestSrcBaseCallCount")
	void testSubtractDestSrcBaseCallCount(BaseCallCount baseCallCount1, 
			Base dest, Base src, BaseCallCount baseCallCount2, 
			BaseCallCount expected) {

		baseCallCount1.subtract(dest, src, baseCallCount2);
		myAssertEquals(expected, baseCallCount1);
	}
	
	/**
	 * Test method for {@link lib.data.count.basecall.BaseCallCount#invert()}.
	 */
	@DisplayName("Should invert base call count correctly")
	@ParameterizedTest(name = "{0} should be invert to {1} ")
	@MethodSource("testInvert")
	void testInvert(BaseCallCount baseCallCount, BaseCallCount expected) {
		baseCallCount.invert();
		myAssertEquals(expected, baseCallCount);
	}
	
	/**
	 * Test method for {@link lib.data.count.basecall.BaseCallCount#getCoverage()}.
	 */
	@DisplayName("Should calculate the correct coverage")
	@ParameterizedTest(name = "BaseCallCount: {0} should have coverage {1}")
	@MethodSource("testGetCoverage")
	void testGetCoverage(BaseCallCount baseCallCount, int expectedCoverage) {
		assertEquals(expectedCoverage, baseCallCount.getCoverage());
	}

	/*
	 * Method source
	 */
	
	Stream<Arguments> testParserParse() {
		return Stream.of(
				Arguments.of("0;0;0;0", create()),
				Arguments.of("1;0;0;0", create().set(Base.A, 1)),
				Arguments.of("1;2;0;0", create().set(Base.A, 1).set(Base.C, 2)),
				Arguments.of(
						"1;2;3;0", 
						create()
							.set(Base.A, 1)
							.set(Base.C, 2)
							.set(Base.G, 3)),
				Arguments.of(
						"1;2;3;4", 
						create()
							.set(Base.A, 1)
							.set(Base.C, 2)
							.set(Base.G, 3)
							.set(Base.T, 4)) );
	}
	
	Stream<Arguments> testParserWrap() {
		return Stream.of(
				Arguments.of(create(), Character.toString(parser.getEmpty())),
				Arguments.of(create().set(Base.A, 1), "1;0;0;0"),
				Arguments.of(create().set(Base.A, 1).set(Base.C, 2), "1;2;0;0"),
				Arguments.of(
						create()
							.set(Base.A, 1)
							.set(Base.C, 2)
							.set(Base.G, 3),
						"1;2;3;0"),
				Arguments.of(
						create()
							.set(Base.A, 1)
							.set(Base.C, 2)
							.set(Base.G, 3)
							.set(Base.T, 4),
						"1;2;3;4") );
	}
	
	Stream<Arguments> testGetAlleles() {
		return Stream.of(
				Arguments.of(parser.parse("0;0;0;0"), new HashSet<Base>()),
				Arguments.of(parser.parse("5;0;0;0"), new HashSet<Base>(Arrays.asList(Base.A))),
				Arguments.of(parser.parse("0;5;0;0"), new HashSet<Base>(Arrays.asList(Base.C))),
				Arguments.of(parser.parse("0;0;10;0"), new HashSet<Base>(Arrays.asList(Base.G))),
				Arguments.of(parser.parse("0;0;0;1"), new HashSet<Base>(Arrays.asList(Base.T))),
				Arguments.of(parser.parse("1;1;1;1"), new HashSet<Base>(Arrays.asList(Base.A, Base.C, Base.G, Base.T))));
	}
	
	Stream<Arguments> testIncrement() {
		return Stream.of(
				Arguments.of(parser.parse("5;0;0;0"), Base.A, parser.parse("6;0;0;0")),
				Arguments.of(parser.parse("0;0;0;0"), Base.C, parser.parse("0;1;0;0")),
				Arguments.of(parser.parse("0;0;10;0"), Base.G, parser.parse("0;0;11;0")),
				Arguments.of(parser.parse("0;0;0;1"), Base.T, parser.parse("0;0;0;2")),
				Arguments.of(parser.parse("1;1;1;1"), Base.A, parser.parse("2;1;1;1")));
	}

	Stream<Arguments> testClear() {
		return Stream.of(
				Arguments.of(parser.parse("0;0;0;0")),
				Arguments.of(parser.parse("1;0;0;0")),
				Arguments.of(parser.parse("0;1;0;0")),
				Arguments.of(parser.parse("0;0;1;0")),
				Arguments.of(parser.parse("0;0;0;1")),
				Arguments.of(parser.parse("1;1;1;1")) );
	}
	
	Stream<Arguments> testSet() {
		return Stream.of(
				Arguments.of(parser.parse("1;0;0;0"), Base.A, 2, parser.parse("2;0;0;0")),
				Arguments.of(parser.parse("0;1;0;0"), Base.C, 3, parser.parse("0;3;0;0")),
				Arguments.of(parser.parse("0;0;1;0"), Base.G, 4, parser.parse("0;0;4;0")),
				Arguments.of(parser.parse("0;0;0;1"), Base.T, 2, parser.parse("0;0;0;2")),
				Arguments.of(parser.parse("1;1;1;1"), Base.A, 5, parser.parse("5;1;1;1")) );
	}

	Stream<Arguments> testAddBaseBaseCallCount() {
		return Stream.of(
				Arguments.of(
						parser.parse("0;1;1;1"), 
						Base.A, 
						parser.parse("2;0;0;0"),
						parser.parse("2;1;1;1")),
				Arguments.of(
						parser.parse("0;1;0;0"), 
						Base.C, 
						parser.parse("1;1;1;1"),
						parser.parse("0;2;0;0")),
				Arguments.of(
						parser.parse("0;0;0;0"), 
						Base.G, 
						parser.parse("1;1;10;1"),
						parser.parse("0;0;10;0")),
				Arguments.of(
						parser.parse("0;1;0;0"), 
						Base.T, 
						parser.parse("1;1;1;1"),
						parser.parse("0;1;0;1")) );
	}

	Stream<Arguments> testAddBaseCallCount() {
		return Stream.of(
				Arguments.of(parser.parse("1;0;0;0"), 
						parser.parse("0;0;1;0"),
						parser.parse("1;0;1;0")),
				Arguments.of(parser.parse("0;1;1;1"), 
						parser.parse("2;0;0;0"),
						parser.parse("2;1;1;1")),
				Arguments.of(parser.parse("0;1;0;0"), 
						parser.parse("1;1;1;1"),
						parser.parse("1;2;1;1")),
				Arguments.of(parser.parse("0;0;0;0"), 
						parser.parse("1;1;10;1"),
						parser.parse("1;1;10;1")),
				Arguments.of(parser.parse("0;1;0;0"), 
						parser.parse("1;1;1;1"),
						parser.parse("1;2;1;1")) );
	}

	Stream<Arguments> testAddDestSrcBaseCallCount() {
		return Stream.of(
				Arguments.of(parser.parse("0;1;1;1"), 
						Base.A,
						Base.A, 
						parser.parse("2;0;0;0"),
						parser.parse("2;1;1;1")),
				Arguments.of(parser.parse("0;1;1;1"), 
						Base.A,
						Base.T, 
						parser.parse("2;0;0;0"),
						parser.parse("0;1;1;1")),
				Arguments.of(parser.parse("0;1;0;0"), 
						Base.C,
						Base.G, 
						parser.parse("1;1;1;1"),
						parser.parse("0;2;0;0")),
				Arguments.of(parser.parse("0;0;0;0"), 
						Base.A, 
						Base.G,
						parser.parse("1;1;10;1"),
						parser.parse("10;0;0;0")),
				Arguments.of(parser.parse("0;1;0;0"), 
						Base.T,
						Base.A,
						parser.parse("1;1;1;1"),
						parser.parse("0;1;0;1")) );
	}

	Stream<Arguments> testSubtractBaseBaseCallCount() {
		return Stream.of(
				Arguments.of(
						parser.parse("0;1;1;1"), 
						Base.T, 
						parser.parse("2;0;0;0"),
						parser.parse("0;1;1;1")),
				Arguments.of(
						parser.parse("1;1;1;1"), 
						Base.C, 
						parser.parse("0;1;1;0"),
						parser.parse("1;0;1;1")),
				Arguments.of(
						parser.parse("1;1;10;2"), 
						Base.G, 
						parser.parse("1;1;1;1"),
						parser.parse("1;1;9;2")),
				Arguments.of(
						parser.parse("0;1;0;2"), 
						Base.T, 
						parser.parse("1;1;1;2"),
						parser.parse("0;1;0;0")) );
	}

	Stream<Arguments> testSubtractBaseCallCount() {
		return Stream.of(
				Arguments.of(parser.parse("1;0;1;0"), 
						parser.parse("0;0;1;0"),
						parser.parse("1;0;0;0")),
				Arguments.of(parser.parse("2;1;1;1"), 
						parser.parse("2;0;0;0"),
						parser.parse("0;1;1;1")),
				Arguments.of(parser.parse("1;1;1;1"), 
						parser.parse("1;1;1;1"),
						parser.parse("0;0;0;0")),
				Arguments.of(parser.parse("10;10;10;10"), 
						parser.parse("1;0;10;0"),
						parser.parse("9;10;0;10")),
				Arguments.of(parser.parse("1;2;3;4"), 
						parser.parse("0;0;0;0"),
						parser.parse("1;2;3;4")) );
	}

	Stream<Arguments> testSubtractDestSrcBaseCallCount() {
		return Stream.of(
				Arguments.of(parser.parse("4;1;1;1"), 
						Base.A,
						Base.A, 
						parser.parse("2;0;0;0"),
						parser.parse("2;1;1;1")),
				Arguments.of(parser.parse("2;1;1;2"), 
						Base.A,
						Base.T, 
						parser.parse("2;2;2;2"),
						parser.parse("0;1;1;2")),
				Arguments.of(parser.parse("4;3;2;1"), 
						Base.C,
						Base.G, 
						parser.parse("4;3;2;1"),
						parser.parse("4;1;2;1")),
				Arguments.of(parser.parse("8;6;4;2"), 
						Base.A, 
						Base.G,
						parser.parse("1;2;3;4"),
						parser.parse("5;6;4;2")),
				Arguments.of(parser.parse("1;1;1;1"), 
						Base.T,
						Base.A,
						parser.parse("1;1;1;1"),
						parser.parse("1;1;1;0")) );
	}
	
	Stream<Arguments> testInvert() {
		return Stream.of(
				Arguments.of(parser.parse("0;0;0;0"), parser.parse("0;0;0;0")),
				Arguments.of(parser.parse("2;0;0;1"), parser.parse("1;0;0;2")),
				Arguments.of(parser.parse("0;5;6;0"), parser.parse("0;6;5;0")),
				Arguments.of(parser.parse("0;0;0;1"), parser.parse("1;0;0;0")),
				Arguments.of(parser.parse("1;2;3;4"), parser.parse("4;3;2;1")) );
	}
	
	Stream<Arguments> testGetCoverage() {
		return Stream.of(
				Arguments.of(parser.parse("0;0;0;0"), 0),
				Arguments.of(parser.parse("1;0;0;1"), 2),
				Arguments.of(parser.parse("5;0;0;5"), 10),
				Arguments.of(parser.parse("0;0;0;1"), 1),
				Arguments.of(parser.parse("1;1;1;1"), 4));
	}

	/*
	 * Helper methods
	 */
	
	public static void myAssertEquals(final List<Integer> expected, final BaseCallCount actual) {
		if (expected.size() != Base.validValues().length) {
			throw new IllegalArgumentException("expectedBaseCall must be of size 4 but is " + expected.size());
		}
		for (final Base base : Base.validValues()) {
			final int expectedCount = expected.get(base.getIndex());
			assertEquals(expectedCount, actual.getBaseCall(base), "Mismatch for base: " + base);
		}
	}
	
	public static void myAssertEquals(final BaseCallCount expected, final BaseCallCount actual) {
		if (expected.equals(actual)) {
			return;
		}

		for (final Base base : Base.validValues()) {
			final int expectedCount = expected.getBaseCall(base);
			final int actualCount = expected.getBaseCall(base);
			assertEquals(expectedCount, actualCount, "Mismatch for base: " + base);
		}
	}
		
}
