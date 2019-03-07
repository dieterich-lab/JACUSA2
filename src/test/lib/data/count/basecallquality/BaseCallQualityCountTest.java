package test.lib.data.count.basecallquality;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import lib.data.count.basecallquality.BaseCallQualityCount;
import lib.phred2prob.Phred2Prob;
import lib.util.Base;

public abstract class BaseCallQualityCountTest {

	/**
	 * Returns specific implementation of BaseCallQualityCount
	 * @param baseCall
	 * @return
	 */
	abstract protected BaseCallQualityCount createBaseCallQualityCount(Map<Base, Map<Byte, Integer>> base2qual2count);

	/*
	 * Tests
	 */
	
	/**
	 * Test method for {@link lib.data.count.basecallquality.BaseCallQualityCount#getAlleles()}.
	 */
	@DisplayName("Should calculate the correct alleles")
	@ParameterizedTest(name = "BaseCallQualityCount: {0} should have the following alleles {1}")
	@MethodSource("testGetAlleles")
	void testGetAlleles(Map<Base, Map<Byte, Integer>> base2qual2count, Set<Base> expectedAlleles) {
		final BaseCallQualityCount baseCallQualityCount = createBaseCallQualityCount(base2qual2count);
		assertEquals(expectedAlleles, baseCallQualityCount.getAlleles());
	}

	/**
	 * Test method for {@link lib.data.count.basecallquality.BaseCallQualityCount#getBaseCallQuality()}.
	 */
	@DisplayName("Should return the correct base call qualitities")
	@ParameterizedTest(name = "{0} should have for the base {1} the following base call qualities {2}")
	@MethodSource("testGetBaseCallQuality")
	void testGetBaseCallQuality(Map<Base, Map<Byte, Integer>> base2qual2count, Base base, Set<Byte> expectedQualities) {
		final BaseCallQualityCount baseCallQualityCount = createBaseCallQualityCount(base2qual2count);
		// assertTrue(Utils.equalSets(expectedQualities, baseCallQualityCount.getBaseCallQuality(base)));
		assertEquals(expectedQualities, baseCallQualityCount.getBaseCallQuality(base));
	}

	/**
	 * Test method for {@link lib.data.count.basecallquality.BaseCallQualityCount#increment()}.
	 */
	@DisplayName("Should increment base call qualitities correctly")
	@ParameterizedTest(name = "{0} should be incremented for the base {1} and base call quality {2}")
	@MethodSource("testIncrement")
	void testIncrement(Map<Base, Map<Byte, Integer>> base2qual2count, Base base, byte qualitiy, Map<Base, Map<Byte, Integer>> expectedBase2qual2count) {
		final BaseCallQualityCount baseCallQualityCount = createBaseCallQualityCount(base2qual2count);
		baseCallQualityCount.increment(base, qualitiy);
		checkEqual(baseCallQualityCount, expectedBase2qual2count);
	}
	
	/**
	 * Test method for {@link lib.data.count.basecallquality.BaseCallQualityCount#clear()}.
	 */
	@DisplayName("Should correctly claer base call quality count")
	@ParameterizedTest(name = "BaseCallQualityCount: {0} should be empty")
	@MethodSource("testClear")
	void testClear(Map<Base, Map<Byte, Integer>> base2qual2count) {
		final BaseCallQualityCount baseCallQualityCount = createBaseCallQualityCount(base2qual2count);
		baseCallQualityCount.clear();
		for (final Base base : Base.validValues()) {
			final int count = baseCallQualityCount.getBaseCallQuality(base).size();
			assertEquals(0, count);
		}
	}

	/**
	 * Test method for {@link lib.data.count.basecallquality.BaseCallQualityCount#set()}.
	 */
	@DisplayName("Should set base quality count correctly")
	@ParameterizedTest(name = "{0} should set base {1} and quality {2} to {3} and result in {4}")
	@MethodSource("testSet")
	void testSet(Map<Base, Map<Byte, Integer>> base2qual2count, 
			Base base, byte quality, int count, 
			Map<Base, Map<Byte, Integer>> expectedBase2qual2count) {
		final BaseCallQualityCount baseCallQualityCount = createBaseCallQualityCount(base2qual2count);
		baseCallQualityCount.set(base, quality, count);
		checkEqual(baseCallQualityCount, expectedBase2qual2count);
	}
	
	/**
	 * Test method for {@link lib.data.count.basecallquality.BaseCallQualityCount#add(Base, BaseCallQualityCount)}.
	 */
	@DisplayName("Should add base call quality count correctly")
	@ParameterizedTest(name = "BaseCallQualityCount: {0} should add base quality counts for base {1} from {2} giving {3} ")
	@MethodSource("testAddBaseBaseCallQualityCount")
	void testAddBaseBaseCallQualityCount(Map<Base, Map<Byte, Integer>> base2qual2count1, 
			Base base, 
			Map<Base, Map<Byte, Integer>> base2qual2count2, 
			Map<Base, Map<Byte, Integer>> expectedBase2qual2count) {
		
		final BaseCallQualityCount baseCallQualityCount1 = createBaseCallQualityCount(base2qual2count1);
		final BaseCallQualityCount baseCallQualityCount2 = createBaseCallQualityCount(base2qual2count2);
		baseCallQualityCount1.add(base, baseCallQualityCount2);
		checkEqual(baseCallQualityCount1, expectedBase2qual2count);
	}

	/**
	 * Test method for {@link lib.data.count.basecallquality.BaseCallQualityCount#add(Set, BaseCallQualityCount)}.
	 */
	@DisplayName("Should add base call quality count correctly")
	@ParameterizedTest(name = "{0} should add base call quality counts for bases {1} from {2} giving {3} ")
	@MethodSource("testAddSetBaseBaseCallQualityCount")
	void testAddSetBaseBaseCallQualityCount(Map<Base, Map<Byte, Integer>> base2qual2count1, 
			Set<Base> bases, 
			Map<Base, Map<Byte, Integer>> base2qual2count2, 
			Map<Base, Map<Byte, Integer>> expectedBase2qual2count) {
		
		final BaseCallQualityCount baseCallQualityCount1 = createBaseCallQualityCount(base2qual2count1);
		final BaseCallQualityCount baseCallQualityCount2 = createBaseCallQualityCount(base2qual2count2);
		baseCallQualityCount1.add(bases, baseCallQualityCount2);
		checkEqual(baseCallQualityCount1, expectedBase2qual2count);
	}
	
	/**
	 * Test method for {@link lib.data.count.basecallquality.BaseCallQualityCount#add(Base, Base, BaseCallQualityCount)}.
	 */
	@DisplayName("Should add base call quality count correctly")
	@ParameterizedTest(name = "BaseCallQualityCount: {0} should add base quality counts for base {1} to base {2} from {3} giving {4} ")
	@MethodSource("testAddBaseBaseBaseCallQualityCount")
	void testAddBaseBaseBaseCallQualityCount(Map<Base, Map<Byte, Integer>> base2qual2count1, 
			Base dest,
			Base src,
			Map<Base, Map<Byte, Integer>> base2qual2count2, 
			Map<Base, Map<Byte, Integer>> expectedBase2qual2count) {
		
		final BaseCallQualityCount baseCallQualityCount1 = createBaseCallQualityCount(base2qual2count1);
		final BaseCallQualityCount baseCallQualityCount2 = createBaseCallQualityCount(base2qual2count2);
		baseCallQualityCount1.add(dest, src, baseCallQualityCount2);
		checkEqual(baseCallQualityCount1, expectedBase2qual2count);
	}
	
	/**
	 * Test method for {@link lib.data.count.basecallquality.BaseCallQualityCount#subtract(Base, BaseCallQualityCount)}.
	 */
	@DisplayName("Should subtract base call quality count correctly")
	@ParameterizedTest(name = "BaseCallQualityCount: {0} should subtract base quality counts for base {1} from {2} giving {3} ")
	@MethodSource("testSubtractBaseBaseCallQualityCount")
	void testSubtractBaseBaseCallQualityCount(Map<Base, Map<Byte, Integer>> base2qual2count1, 
			Base base, 
			Map<Base, Map<Byte, Integer>> base2qual2count2, 
			Map<Base, Map<Byte, Integer>> expectedBase2qual2count) {
		
		final BaseCallQualityCount baseCallQualityCount1 = createBaseCallQualityCount(base2qual2count1);
		final BaseCallQualityCount baseCallQualityCount2 = createBaseCallQualityCount(base2qual2count2);
		baseCallQualityCount1.subtract(base, baseCallQualityCount2);
		checkEqual(baseCallQualityCount1, expectedBase2qual2count);
	}

	/**
	 * Test method for {@link lib.data.count.basecallquality.BaseCallQualityCount#subtract(Set, BaseCallQualityCount)}.
	 */
	@DisplayName("Should subtract base call quality count correctly")
	@ParameterizedTest(name = "BaseCallQualityCount: {0} should subtract base quality counts for bases {1} from {2} giving {3} ")
	@MethodSource("testSubtractSetBaseBaseCallQualityCount")
	void testSubtractSetBaseBaseCallQualityCount(Map<Base, Map<Byte, Integer>> base2qual2count1, 
			Set<Base> bases, 
			Map<Base, Map<Byte, Integer>> base2qual2count2, 
			Map<Base, Map<Byte, Integer>> expectedBase2qual2count) {
		
		final BaseCallQualityCount baseCallQualityCount1 = createBaseCallQualityCount(base2qual2count1);
		final BaseCallQualityCount baseCallQualityCount2 = createBaseCallQualityCount(base2qual2count2);
		baseCallQualityCount1.subtract(bases, baseCallQualityCount2);
		checkEqual(baseCallQualityCount1, expectedBase2qual2count);
	}
	
	/**
	 * Test method for {@link lib.data.count.basecallquality.BaseCallQualityCount#subtract(Base, Base, BaseCallQualityCount)}.
	 */
	@DisplayName("Should subtract base call quality count correctly")
	@ParameterizedTest(name = "BaseCallQualityCount: {0} should subtract base quality counts for base {1} to base {2} from {3} giving {4} ")
	@MethodSource("testSubtractBaseBaseBaseCallQualityCount")
	void testSubtractBaseBaseBaseCallQualityCount(Map<Base, Map<Byte, Integer>> base2qual2count1, 
			Base dest,
			Base src,
			Map<Base, Map<Byte, Integer>> base2qual2count2, 
			Map<Base, Map<Byte, Integer>> expectedBase2qual2count) {
		
		final BaseCallQualityCount baseCallQualityCount1 = createBaseCallQualityCount(base2qual2count1);
		final BaseCallQualityCount baseCallQualityCount2 = createBaseCallQualityCount(base2qual2count2);
		baseCallQualityCount1.subtract(dest, src, baseCallQualityCount2);
		checkEqual(baseCallQualityCount1, expectedBase2qual2count);
	}
	
	/**
	 * Test method for {@link lib.data.count.basecallquality.BaseCallQualityCount#invert()}.
	 */
	@DisplayName("Should invert base call quality count correctly")
	@ParameterizedTest(name = "Base call quality count: {0} should be invert to {1} ")
	@MethodSource("testInvert")
	void testInvert(Map<Base, Map<Byte, Integer>> base2qual2count, Map<Base, Map<Byte, Integer>> expectedBase2qual2count) {
		final BaseCallQualityCount baseCallQualityCount = createBaseCallQualityCount(base2qual2count);
		baseCallQualityCount.invert();
		checkEqual(baseCallQualityCount, expectedBase2qual2count);
	}
	
	/*
	 * Method source
	 */
	
	static Stream<Arguments> testGetAlleles() {
		// expected
		return Stream.of(
				Arguments.of(create(new int[][] { {}, {}, {}, {} }), new HashSet<Base>()),
				Arguments.of(create(new int[][] { {40, 1}, {}, {}, {} }), new HashSet<Base>(Arrays.asList(Base.A))),
				Arguments.of(create(new int[][] { {}, {40, 1}, {}, {} }), new HashSet<Base>(Arrays.asList(Base.C))),
				Arguments.of(create(new int[][] { {}, {}, {40, 1}, {} }), new HashSet<Base>(Arrays.asList(Base.G))),
				Arguments.of(create(new int[][] { {}, {}, {}, {40, 1} }), new HashSet<Base>(Arrays.asList(Base.T))),
				Arguments.of(create(new int[][] { {40, 1, 1, 1},{}, {}, {} }), new HashSet<Base>(Arrays.asList(Base.A))),
				Arguments.of(create(new int[][] { {40, 1},{}, {}, {1, 1} }), new HashSet<Base>(Arrays.asList(Base.A, Base.T))),
				Arguments.of(create(new int[][] { {40, 1}, {40, 1}, {40, 1}, {40, 1} }), new HashSet<Base>(Arrays.asList(Base.A, Base.C, Base.G, Base.T))));
	}

	static Stream<Arguments> testGetBaseCallQuality() {
		return Stream.of(
				Arguments.of(create(new int[][] { {}, {}, {}, {} }), Base.A, new HashSet<>()),
				Arguments.of(create(new int[][] { {}, {}, {}, {} }), Base.C, new HashSet<>()),
				Arguments.of(create(new int[][] { {}, {}, {}, {} }), Base.G, new HashSet<>()),
				Arguments.of(create(new int[][] { {}, {}, {}, {} }), Base.T, new HashSet<>()),
				Arguments.of(create(new int[][] { {10, 1}, {}, {}, {} }), Base.A, new HashSet<>(Arrays.asList((byte)10))),
				Arguments.of(create(new int[][] { {}, {20, 1}, {}, {} }), Base.C, new HashSet<>(Arrays.asList((byte)20))),
				Arguments.of(create(new int[][] { {}, {}, {30, 1}, {} }), Base.G, new HashSet<>(Arrays.asList((byte)30))),
				Arguments.of(create(new int[][] { {}, {}, {}, {40, 1} }), Base.T, new HashSet<>(Arrays.asList((byte)40))),
				Arguments.of(create(new int[][] { {10, 1}, {}, {}, {} }), Base.C, new HashSet<>()),
				Arguments.of(create(new int[][] { {}, {20, 1}, {}, {} }), Base.G, new HashSet<>()),
				Arguments.of(create(new int[][] { {}, {}, {30, 1}, {} }), Base.T, new HashSet<>()),
				Arguments.of(create(new int[][] { {}, {}, {}, {40, 1} }), Base.A, new HashSet<>()),
				Arguments.of(create(new int[][] { {10, 1}, {20, 1}, {30, 1}, {40, 1} }), Base.A, new HashSet<>(Arrays.asList((byte)10))),
				Arguments.of(create(new int[][] { {10, 1}, {20, 1}, {30, 1}, {40, 1} }), Base.C, new HashSet<>(Arrays.asList((byte)20))),
				Arguments.of(create(new int[][] { {10, 1}, {20, 1}, {30, 1}, {40, 1} }), Base.G, new HashSet<>(Arrays.asList((byte)30))),
				Arguments.of(create(new int[][] { {10, 1}, {20, 1}, {30, 1}, {40, 1} }), Base.T, new HashSet<>(Arrays.asList((byte)40))),
				Arguments.of(create(new int[][] { {10, 1}, {20, 1, 21, 1, 22, 1, 23, 1, 24, 1, 25, 1}, {30, 1}, {40, 1} }), 
						Base.C, new HashSet<>(Arrays.asList((byte)20, (byte)21, (byte)22, (byte)23, (byte)24, (byte)25))));
	}
	
	static Stream<Arguments> testIncrement() {
		return Stream.of(
				Arguments.of(create(new int[][] { {}, {}, {}, {} }), 
						Base.A, (byte)10, 
						create(new int[][] { {10, 1}, {}, {}, {} })),
				Arguments.of(create(new int[][] { {10, 1}, {}, {}, {} }), 
						Base.A, (byte)10, 
						create(new int[][] { {10, 2}, {}, {}, {} })),
				Arguments.of(create(new int[][] { {20, 1}, {20, 1}, {20, 1}, {20, 1} }), 
						Base.A, (byte)10, 
						create(new int[][] { {10, 1, 20, 1}, {20, 1}, {20, 1}, {20, 1} })),
				Arguments.of(create(new int[][] { {20, 1}, {20, 1}, {20, 1}, {20, 1} }), 
						Base.C, (byte)10, 
						create(new int[][] { {20, 1}, {10, 1, 20, 1}, {20, 1}, {20, 1} })),
				Arguments.of(create(new int[][] { {20, 1}, {20, 1}, {20, 1}, {20, 1} }), 
						Base.G, (byte)10, 
						create(new int[][] { {20, 1}, {20, 1}, {10, 1, 20, 1}, {20, 1} })),				
				Arguments.of(create(new int[][] { {20, 1}, {20, 1}, {20, 1}, {20, 1} }), 
						Base.T, (byte)10, 
						create(new int[][] { {20, 1}, {20, 1}, {20, 1}, {10, 1, 20, 1} })));
	}
	
	static Stream<Arguments> testClear() {
		return Stream.of(
				Arguments.of(create(new int[][] { {}, {}, {}, {} })),
				Arguments.of(create(new int[][] { {10, 1, 20, 1, 30, 1, 40, 1}, {5, 1, 15, 1, 25, 1, 35, 1}, {1, 1, 2, 1, 3, 1, 4, 1}, {1, 1, 1, 1, 1, 1, 1, 1} })),
				Arguments.of(create(new int[][] { {10, 1}, {20, 1}, {30, 1}, {40, 1} })));
	}

	static Stream<Arguments> testSet() {
		return Stream.of(
				Arguments.of(
						create(new int[][] { {40, 1}, {30, 1}, {20, 1}, {10, 1} }), 
						Base.A, (byte)40, 0, 
						create(new int[][] { {40, 0}, {30, 1}, {20, 1}, {10, 1} })),				
				Arguments.of(
						create(new int[][] { {40, 1}, {30, 1}, {20, 1}, {10, 1} }), 
						Base.C, (byte)30, 0, 
						create(new int[][] { {40, 1}, {30, 0}, {20, 1}, {10, 1} })),
				Arguments.of(
						create(new int[][] { {40, 1}, {30, 1}, {20, 1}, {10, 1} }), 
						Base.G, (byte)20, 0, 
						create(new int[][] { {40, 1}, {30, 1}, {20, 0}, {10, 1} })),
				Arguments.of(
						create(new int[][] { {40, 1}, {30, 1}, {20, 1}, {10, 1} }), 
						Base.T, (byte)10, 0, 
						create(new int[][] { {40, 1}, {30, 1}, {20, 1}, {10, 0} })));
	}
	
	static Stream<Arguments> testAddBaseBaseCallQualityCount() {
		return Stream.of(
				Arguments.of(
						create(new int[][] { {40, 1}, {30, 1}, {20, 1}, {10, 1} }), 
						Base.A, create(new int[][] { {40, 1}, {30, 1}, {20, 1}, {10, 1} }), 
						create(new int[][] { {40, 2}, {30, 1}, {20, 1}, {10, 1} })),
				Arguments.of(
						create(new int[][] { {40, 1}, {30, 1}, {20, 1}, {10, 1} }), 
						Base.C, create(new int[][] { {40, 1}, {30, 1}, {20, 1}, {10, 1} }), 
						create(new int[][] { {40, 1}, {30, 2}, {20, 1}, {10, 1} })),
				Arguments.of(
						create(new int[][] { {40, 1}, {30, 1}, {20, 1}, {10, 1} }), 
						Base.G, create(new int[][] { {40, 1}, {30, 1}, {20, 1}, {10, 1} }), 
						create(new int[][] { {40, 1}, {30, 1}, {20, 2}, {10, 1} })),
				Arguments.of(
						create(new int[][] { {40, 1}, {30, 1}, {20, 1}, {10, 1} }), 
						Base.T, create(new int[][] { {40, 1}, {30, 1}, {20, 1}, {10, 1} }), 
						create(new int[][] { {40, 1}, {30, 1}, {20, 1}, {10, 2} })));
	}
	
	static Stream<Arguments> testAddSetBaseBaseCallQualityCount() {
		return Stream.of(
				Arguments.of(
						create(new int[][] { {40, 1}, {30, 1}, {20, 1}, {10, 1} }), 
						new HashSet<Base>(Arrays.asList(Base.validValues())), 
						create(new int[][] { {40, 1}, {30, 1}, {20, 1}, {10, 1} }), 
						create(new int[][] { {40, 2}, {30, 2}, {20, 2}, {10, 2} })),
				Arguments.of(
						create(new int[][] { {40, 1}, {30, 1}, {20, 1}, {10, 1} }), 
						new HashSet<Base>(Arrays.asList(Base.validValues())), 
						create(new int[][] { {10, 1}, {20, 1}, {30, 1}, {40, 1} }), 
						create(new int[][] { {10, 1, 40, 1}, {20, 1, 30, 1}, {20, 1, 30, 1}, {10, 1, 40, 1} })));
	}

	static Stream<Arguments> testAddBaseBaseBaseCallQualityCount() {
		return Stream.of(
				Arguments.of(
						create(new int[][] { {40, 1}, {30, 1}, {30, 1}, {30, 1} }), 
						Base.A,
						Base.T,
						create(new int[][] { {40, 1}, {30, 1}, {30, 1}, {30, 1} }), 
						create(new int[][] { {30, 1, 40, 1}, {30, 1}, {30, 1}, {30, 1} })),
				Arguments.of(
						create(new int[][] { {40, 1}, {30, 1}, {30, 1}, {30, 1} }), 
						Base.G,
						Base.C,
						create(new int[][] { {40, 1}, {30, 1}, {30, 1}, {30, 1} }), 
						create(new int[][] { {40, 1}, {30, 1}, {30, 2}, {30, 1} })));
	}
	
	static Stream<Arguments> testSubtractBaseBaseCallQualityCount() {
		return Stream.of(
				Arguments.of(
						create(new int[][] { {40, 2}, {30, 1}, {30, 1}, {30, 1} }), 
						Base.A, 
						create(new int[][] { {40, 1}, {30, 1}, {30, 1}, {30, 1} }), 
						create(new int[][] { {40, 1}, {30, 1}, {30, 1}, {30, 1} })),						
				Arguments.of(
						create(new int[][] { {40, 1}, {30, 1, 20, 1}, {30, 1}, {30, 1} }), 
						Base.C, 
						create(new int[][] { {40, 1}, {30, 1}, {30, 1}, {30, 1} }), 
						create(new int[][] { {40, 1}, {20, 1}, {30, 1}, {30, 1} })));
	}
	
	static Stream<Arguments> testSubtractSetBaseBaseCallQualityCount() {
		return Stream.of(
				Arguments.of(
						create(new int[][] { {40, 2}, {30, 2}, {30, 2}, {30, 2} }), 
						new HashSet<Base>(Arrays.asList(Base.validValues())), 
						create(new int[][] { {40, 1}, {30, 1}, {30, 1}, {30, 1} }), 
						create(new int[][] { {40, 1}, {30, 1}, {30, 1}, {30, 1} })),						
				Arguments.of(
						create(new int[][] { {40, 1}, {30, 1, 20, 1}, {30, 1}, {30, 1} }), 
						new HashSet<Base>(Arrays.asList(Base.C)), 
						create(new int[][] { {40, 1}, {30, 1}, {30, 1}, {30, 1} }), 
						create(new int[][] { {40, 1}, {20, 1}, {30, 1}, {30, 1} })));
	}

	static Stream<Arguments> testSubtractBaseBaseBaseCallQualityCount() {
		return Stream.of(
				Arguments.of(
						create(new int[][] { {40, 10, 30, 10}, {40, 20}, {40, 30}, {40, 40, 30, 40} }), 
						Base.T,
						Base.A,
						create(new int[][] { {40, 10}, {40, 20}, {40, 30}, {40, 40} }), 
						create(new int[][] { {40, 10}, {40, 20}, {40, 30}, {40, 30, 30, 40} })),
				Arguments.of(
						create(new int[][] { {40, 10}, {40, 20}, {40, 30}, {40, 40} }), 
						Base.G,
						Base.A,
						create(new int[][] { {40, 10}, {40, 20}, {40, 30}, {40, 40} }), 
						create(new int[][] { {40, 10}, {40, 20}, {40, 20}, {40, 40} })),
				Arguments.of(
						create(new int[][] { {40, 10}, {40, 20}, {40, 30}, {40, 40} }), 
						Base.G,
						Base.C,
						create(new int[][] { {40, 10}, {40, 20}, {40, 30}, {40, 40} }), 
						create(new int[][] { {40, 10}, {40, 20}, {40, 10}, {40, 40} })));
	}
	
	static Stream<Arguments> testInvert() {
		return Stream.of(
				Arguments.of(
						create(new int[][] { {0, 4, 40, 1}, {1, 3, 30, 2}, {2, 2, 20, 3}, {3, 1, 10, 4} }), 
						create(new int[][] { {3, 1, 10, 4}, {2, 2, 20, 3}, {1, 3, 30, 2}, {0, 4, 40, 1} })));
	}

	/*
	 * Helper methods
	 */

	void checkEqual(final BaseCallQualityCount baseCallQualityCount, final Map<Base, Map<Byte, Integer>> base2qual2count) {
		for (final Base base : base2qual2count.keySet()) {
			final Map<Byte, Integer> qual2count = base2qual2count.get(base);
			for (final byte quality : qual2count.keySet()) {
				final int expectedCount = qual2count.get(quality);
				assertEquals(expectedCount, baseCallQualityCount.getBaseCallQuality(base, quality));
			}
		}
	}

	/*
	 * Toy data structure for base -> quality -> count
	 */

	static Map<Base, Map<Byte, Integer>> createEmpty() {
		final Map<Base, Map<Byte, Integer>> base2qual2count = 
				new HashMap<Base, Map<Byte, Integer>>(Base.validValues().length);
		
		final int n = Base.validValues().length;
		for (int baseIndex = 0; baseIndex < n; ++baseIndex) {
			final Base base = Base.valueOf(baseIndex);
			final Map<Byte, Integer> qual2count = new HashMap<Byte, Integer>(Phred2Prob.MAX_Q);
			base2qual2count.put(base, qual2count);
			
		}
		return base2qual2count;
	}
	
	// format of create:
	// A {byte, count}, {byte, count},{byte, count}
	// C {byte, count}, {byte, count}
	// G {byte, count}, 
	// T {}
	static Map<Base, Map<Byte, Integer>> create(final int[][] arrayBase2qual2count) {
		final Map<Base, Map<Byte, Integer>> base2qual2count = createEmpty();
		final int n = Base.validValues().length;
		if (arrayBase2qual2count.length != n) {
			throw new IllegalArgumentException("arrayBase2qual2count must be of size " + n + " but is " + arrayBase2qual2count.length);
		}
		for (int baseIndex = 0; baseIndex < n; ++baseIndex) {
			final Base base = Base.valueOf(baseIndex);
			final int[] qual2count = arrayBase2qual2count[baseIndex];
			if (qual2count.length % 2 != 0) {
				throw new IllegalArgumentException("wrong format for arrayBase2qual2count; qual2count.length % 2 != 0 " + qual2count.length);
			}

			for (int j = 0; j < qual2count.length; j += 2) {
				if(j > Phred2Prob.MAX_Q) {
					throw new IllegalArgumentException("arrayBase2qual2count contains qualities > " + Phred2Prob.MAX_Q);
				}
				final byte quality = (byte)arrayBase2qual2count[baseIndex][j];
				final int count = arrayBase2qual2count[baseIndex][j + 1];
				addBaseCallQuality(base, quality, count, base2qual2count);
			}
		}
		return base2qual2count;
	}

	static void addBaseCallQuality(final Map<Base, Map<Byte, Integer>> base2qual2count, final Base base, List<Byte> qualities, List<Integer> counts) {
		if (qualities.size() != counts.size()) {
			throw new IllegalArgumentException("qualities (" + qualities.size() + ") and counts (" + counts.size() + ") must have same size!"); 
		}
		
		final int n = qualities.size();
		for (int i = 0; i < n; ++i) {
			final byte quality = qualities.get(i);
			final int count = counts.get(i);
			addBaseCallQuality(base, quality, count, base2qual2count);	
		}
	}

	static void addBaseCallQuality(final Base base, byte quality, final Map<Base, Map<Byte, Integer>> base2qual2count) {
		addBaseCallQuality(base, quality, 1, base2qual2count);
	}

	static void addBaseCallQuality(final Base base, final byte quality, final int count, final Map<Base, Map<Byte, Integer>> base2qual2count) {
		if (! base2qual2count.containsKey(base)) {
			throw new IllegalArgumentException("Base " + base + " is missing in base2qual2count");
		}
		final Map<Byte, Integer> qual2count = base2qual2count.get(base);
		if (! qual2count.containsKey(quality)) {
			qual2count.put(quality, 0);
		}
		final int newCount = qual2count.get(quality) + count;
		qual2count.put(quality, newCount);
	}
	
	static String toString(final Map<Base, Map<Byte, Integer>> base2qual2count) {
		final StringBuilder sb = new StringBuilder();
		for (int baseIndex = 0; baseIndex < base2qual2count.size(); ++baseIndex) {
			final Base base = Base.valueOf(baseIndex);
			sb.append(base);
			final Map<Byte, Integer> qual2count = base2qual2count.get(base);
			for (final byte baseQual : qual2count.keySet()) {
				final int count = qual2count.get(baseQual);
				sb.append(' ');
				sb.append(baseQual);
				sb.append('=');
				sb.append(count);
			}
			if (qual2count.size() == 0) {
				sb.append(' ');
				sb.append("empty");
			}
			sb.append('\n');
		}
		
		return sb.toString();
	}
	
}
