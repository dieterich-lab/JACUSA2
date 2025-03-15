package test.lib.estimate;

import lib.estimate.MinkaEstimateDirMultAlpha;
import lib.estimate.MinkaParameter;
import lib.stat.estimation.EstimationContainer;
import lib.stat.estimation.FastEstimationResult;
import lib.stat.nominal.NominalData;
import lib.util.ExtendedInfo;
import test.lib.stat.dirmult.NominalDataArgumentConverter;
import test.utlis.DoubleArrayArgumentConverter;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.CsvFileSource;

class MinkaEstimateDirMultAlphaTest {

	public static final double DELTA_ALPHA = 1e-6;
	public static final double DELTA_LL = 1e-2;

	private final MinkaParameter minkaParameter;
	private MinkaEstimateDirMultAlpha testInstance;
	
	public MinkaEstimateDirMultAlphaTest() {
		minkaParameter = new MinkaParameter();
		testInstance = new MinkaEstimateDirMultAlpha(minkaParameter);
	}
	
	// resources = "src/test/jacusa/estimate/dataMaximizeLogLikelihood.csv",
	@Disabled
	/**
	 * Tests @see lib.estimate.MinkaEstimateDirMultAlpha#maximizeLogLikelihood(EstimationContainer, Info, boolean)
	 */
	@DisplayName("Should calculate the correct alpha(s) and log likelihood")
	@ParameterizedTest(name = "Test on: {arguments}")
	@CsvFileSource(
			resources = "dataMaximizeLogLikelihood.csv",
			delimiter = '\t')
	void testMaximizeLogLikelihood(
			@ConvertWith(NominalDataArgumentConverter.class) NominalData nominalData, 
			@ConvertWith(DoubleArrayArgumentConverter.class) double[] initAlpha, 
			@ConvertWith(DoubleArrayArgumentConverter.class) double[] expectedAlpha, 
			double expectedLL) {
		
		final EstimationContainer estimationContainer = 
				new FastEstimationResult("TEST", nominalData, minkaParameter.getMaxIterations());  
		estimationContainer.add(initAlpha, Double.NaN);
		final ExtendedInfo resultInfo = new ExtendedInfo(null); // FIXME one replicate
		testInstance.maximizeLogLikelihood(estimationContainer, resultInfo, false);
		
		final double[] actualAlpha = estimationContainer.getAlpha();
		assertArrayEquals(expectedAlpha, actualAlpha, DELTA_ALPHA);
		
		final double calculatedLL = estimationContainer.getLogLikelihood();
		assertEquals(expectedLL, calculatedLL, DELTA_LL);
	}
	
	@DisplayName("Should calculate the correct log likelihood")
	@ParameterizedTest(name = "Test on GetLogLikelihood: {arguments}")
	@CsvFileSource(
			resources = "dataGetLogLikelihood.csv",
			delimiter = '\t')
	void testGetLogLikelihood(
			@ConvertWith(NominalDataArgumentConverter.class) NominalData dirMultData, 
			@ConvertWith(DoubleArrayArgumentConverter.class) double[] alpha, 
			double expectedLL) {

		final double calculatedLL = testInstance.getLogLikelihood(alpha, dirMultData);
		assertEquals(expectedLL, calculatedLL, 0.01);
	}
	
}
