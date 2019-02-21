package test.lib.stat.dirmult;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.util.stream.Stream;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import lib.stat.nominal.NominalData;

@TestInstance(Lifecycle.PER_CLASS)
class NominalDataTest {

	public static final double DELTA = 1e-15;

	@ParameterizedTest(name = "DirMultData: {0} should have the following row wise sums {1}")
	/*
	 *  Format:
	 *  1. int(# of categories),double[][](values)
	 *  2. double[](expected rowSums)
	 */
	@MethodSource("testGetRowWiseSums")
	void testGetRowWiseSums(NominalData dirMultData, double[] expected) {
		final double[] actual = dirMultData.getRowWiseSums();
		assertArrayEquals(expected, actual, DELTA);
	}

	Stream<Arguments> testGetRowWiseSums() {
		return Stream.of(
				createArguments(
						4, 
						new double[][] {
							{ 1d, 1d, 1d, 1d }, 
							{ 2d, 2d, 2d, 2d } }, 
						new double[] {4d, 8d} ),
				createArguments(
						2, 
						new double[][] {
							{ 1d, 1d },
							{ 1d, 2d }, 
							{ 3d, 4d } }, 
						new double[] {2d, 3d, 7d} ) );
	}

	Arguments createArguments(final int k, final double[][] data, double[] expected) {
		return Arguments.of(
				NominalData.build(k, data),
				expected);
	}
	
}
