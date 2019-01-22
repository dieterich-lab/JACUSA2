package test.jacusa.estimate;

import lib.stat.dirmult.DirMultData;
import lib.stat.dirmult.DirMultSample;
import lib.stat.dirmult.FastDirMultSample;
import lib.util.Info;
import test.lib.stat.dirmult.DirMultDataArgumentConverter;
import test.utlis.DoubleArrayArgumentConverter;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.CsvFileSource;

import jacusa.estimate.MinkaEstimateDirMultAlpha;
import jacusa.estimate.MinkaEstimateParameter;


class MinkaEstimateDirMultAlphaTest {

	public static final double DELTA_ALPHA = 1e-6;
	public static final double DELTA_LL = 1e-2;

	private final MinkaEstimateParameter minkaParameter;
	private MinkaEstimateDirMultAlpha testInstance;
	
	public MinkaEstimateDirMultAlphaTest() {
		minkaParameter = new MinkaEstimateParameter();
		testInstance = new MinkaEstimateDirMultAlpha(minkaParameter);
	}
	
	// resources = "src/test/jacusa/estimate/dataMaximizeLogLikelihood.csv",
	/**
	 * Tests @see jacusa.estimate.MinkaEstimateDirMultAlpha#maximizeLogLikelihood(DirMultSample, Info, boolean)
	 */
	@DisplayName("Should calculate the correct alpha(s) and log likelihood")
	@ParameterizedTest(name = "Test on: {arguments}")
	@CsvFileSource(
			resources = "dataMaximizeLogLikelihood.csv",
			delimiter = '\t')
	void testMaximizeLogLikelihood(
			@ConvertWith(DirMultDataArgumentConverter.class) DirMultData dirMultData, 
			@ConvertWith(DoubleArrayArgumentConverter.class) double[] initAlpha, 
			@ConvertWith(DoubleArrayArgumentConverter.class) double[] expectedAlpha, 
			double expectedLL) {
		
		final DirMultSample dirMultSample = 
				new FastDirMultSample("TEST", dirMultData, minkaParameter.getMaxIterations());  
		dirMultSample.add(initAlpha, Double.NaN);
		testInstance.maximizeLogLikelihood(dirMultSample, new Info(), false);
		
		final double[] actualAlpha = dirMultSample.getAlpha();
		assertArrayEquals(expectedAlpha, actualAlpha, DELTA_ALPHA);
		
		final double calculatedLL = dirMultSample.getLogLikelihood();
		assertEquals(expectedLL, calculatedLL, DELTA_LL);
	}
	
	@DisplayName("Should calculate the correct log likelihood")
	@ParameterizedTest(name = "Test on GetLogLikelihood: {arguments}")
	@CsvFileSource(
			resources = "dataGetLogLikelihood.csv",
			delimiter = '\t')
	void testGetLogLikelihood(
			@ConvertWith(DirMultDataArgumentConverter.class) DirMultData dirMultData, 
			@ConvertWith(DoubleArrayArgumentConverter.class) double[] alpha, 
			double expectedLL) {

		final double calculatedLL = testInstance.getLogLikelihood(alpha, dirMultData);
		assertEquals(expectedLL, calculatedLL, 0.01);
	}
	
}
