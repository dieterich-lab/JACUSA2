package test.lib.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import htsjdk.samtools.util.SequenceUtil;
import lib.util.Base;

// JUNIT: ongoing A
class BaseTest {

	@DisplayName("Test getC")
	@ParameterizedTest(name = "Base {0} should be {1}")
	@MethodSource("testGetC")
	void testGetC(Base base, byte expected) {
		final byte actual = base.getByte();
		assertEquals(expected, actual);
	}

	@DisplayName("Test getIndex")
	@ParameterizedTest(name = "Base {0} should have index {1}")
	@MethodSource("testGetIndex")
	void testGetIndex(Base base, int expected) {
		final int actual = base.getIndex();
		assertEquals(expected, actual);
	}

	@DisplayName("Test getComplement")
	@ParameterizedTest(name = "Base {0} should have complement {1}")
	@MethodSource("testGetComplement")
	void testGetComplement(Base base, Base expected) {
		final Base actual = base.getComplement();
		assertEquals(expected, actual);
	}

	@DisplayName("Test valueOf(byte)")
	@ParameterizedTest(name = "Byte {0} should be base {1}")
	@MethodSource("testValueOfByte")
	void testValueOfByte(byte c, Base expected) {
		final Base actual = Base.valueOf(c);
		assertEquals(expected, actual);
	}

	@DisplayName("Test valueOf(int)")
	@ParameterizedTest(name = "Int {0} should be base {1}")
	@MethodSource("testValueOfInt")
	void testValueOfByte(int i, Base expected) {
		final Base actual = Base.valueOf(i);
		assertEquals(expected, actual);
	}

	@Test
	@DisplayName("Test validValues == SequenceUtil.VALID_BASES_UPPER from htsjdk")
	void testValidValues() {
		final Set<Byte> actual = Arrays.asList(Base.validValues()).stream()
			.map(b -> b.getByte())
			.collect(Collectors.toSet());
		final Set<Byte> expected = new HashSet<>();
		for (final byte c : SequenceUtil.VALID_BASES_UPPER) {
			expected.add(c);
		}
		assertEquals(expected, actual);
	}

	@DisplayName("Test getNonRefBases")
	@ParameterizedTest(name = "For base {0} the other bases are {1}")
	@MethodSource("getNonRefBases")
	void testGetNonRefBases(Base base, Set<Base> expected) {
		final Set<Base> actual = Base.getNonRefBases(base);
		assertEquals(expected, actual);
	}

	/*
	 * Method Source
	 */
	
	static Stream<Arguments> testGetC() {
		return Stream.of(
				Arguments.of(Base.A, SequenceUtil.A),
				Arguments.of(Base.C, SequenceUtil.C),
				Arguments.of(Base.G, SequenceUtil.G),
				Arguments.of(Base.T, SequenceUtil.T),
				Arguments.of(Base.N, SequenceUtil.N) );
	}
		
	static Stream<Arguments> testGetIndex() {
		return Stream.of(
				Arguments.of(Base.A, 0),
				Arguments.of(Base.C, 1),
				Arguments.of(Base.G, 2),
				Arguments.of(Base.T, 3),
				Arguments.of(Base.N, 4));
	}
	
	static Stream<Arguments> testGetComplement() {
		return Stream.of(
				Arguments.of(Base.T, Base.A),
				Arguments.of(Base.G, Base.C),
				Arguments.of(Base.C, Base.G),
				Arguments.of(Base.A, Base.T),
				Arguments.of(Base.N, Base.N) );
	}
	
	static Stream<Arguments> testValueOfByte() {
		return Stream.of(
				Arguments.of(SequenceUtil.A, Base.A),
				Arguments.of(SequenceUtil.C, Base.C),
				Arguments.of(SequenceUtil.G, Base.G),
				Arguments.of(SequenceUtil.T, Base.T),
				Arguments.of(SequenceUtil.N, Base.N) );
	}
	
	static Stream<Arguments> testValueOfInt() {
		return Stream.of(
				Arguments.of(0, Base.A),
				Arguments.of(1, Base.C),
				Arguments.of(2, Base.G),
				Arguments.of(3, Base.T),
				Arguments.of(4, Base.N) );
	}
	
	static Stream<Arguments> getNonRefBases() {
		return Stream.of(
				Arguments.of(Base.A, new HashSet<>(Arrays.asList(Base.C, Base.G, Base.T))),
				Arguments.of(Base.C, new HashSet<>(Arrays.asList(Base.A, Base.G, Base.T))),
				Arguments.of(Base.G, new HashSet<>(Arrays.asList(Base.A, Base.C, Base.T))),
				Arguments.of(Base.T, new HashSet<>(Arrays.asList(Base.A, Base.C, Base.G))),
				Arguments.of(Base.N, new HashSet<>(Arrays.asList(Base.N))) );
	}
	
}