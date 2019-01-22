package test.lib.stat.dirmult;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.CsvSource;

import lib.stat.dirmult.DirMultData;

// JUNIT: A
class DirMultDataTest {

	public static final double DELTA = 1e-15;

	@ParameterizedTest(name = "DirMultData: {0} should have the following row wise sums {1}")
	/*
	 *  Format:
	 *  1. int(# of categories),double[][](values)
	 *  2. double[](expected rowSums)
	 */
	@CsvSource(
			value = {
					"4,1,1,1,1,2,2,2,2	4,8",
					"2,0,0,1,2,3,4	4,6"
			}, 
			delimiter = '\t')
	void testGetRowWiseSums(
			@ConvertWith(DirMultDataArgumentConverter.class) DirMultData dirMultData, 
			double[] expected) {
		
		final double[] actual = dirMultData.getRowWiseSums();
		assertArrayEquals(expected, actual, DELTA);
	}

}
