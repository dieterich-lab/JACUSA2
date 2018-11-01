package test.lib.stat.dirmult;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import lib.stat.dirmult.DirMultData;
import lib.util.Base;

public class DirMultDataTest {

	public static final double DELTA = 1e-15;

	/**
	 * Test method for {@link lib.stat.dirmult.DirMultData#getRowWiseSums()}.
	 */
	@DisplayName("Should calculate the correct row wise sums")
	@ParameterizedTest(name = "DirMultData: {0} should have the following row wise sums {1}")
	@MethodSource("testGetRowWiseSums")
	void testGetRowWiseSums(DirMultData dirMultData, double[] expectedSums) {
		final double[] calculatedSums = dirMultData.getRowWiseSums();
		
		assertEquals(expectedSums.length, calculatedSums.length);
		for  (int i = 0; i < expectedSums.length; ++i) {
			assertEquals(expectedSums[i], calculatedSums[i], DELTA);
		}
	}

	static Stream<Arguments> testGetRowWiseSums() {
		final int n = Base.validValues().length;
		return Stream.of(
				Arguments.of(
						new DirMultData(n, new double[][] { {1d, 1d, 1d, 1d}, 
															{2d, 2d, 2d, 2d} } ),
						new double[] {4d, 8d}));
	}

}
