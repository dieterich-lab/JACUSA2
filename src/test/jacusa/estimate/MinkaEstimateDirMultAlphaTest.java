package test.jacusa.estimate;

import lib.stat.dirmult.DirMultData;
import lib.stat.dirmult.DirMultSample;
import lib.stat.dirmult.FastDirMultSample;
import lib.util.Info;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.FileNotFoundException;
import java.util.Iterator;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import jacusa.estimate.MinkaEstimateDirMultAlpha;
import jacusa.estimate.MinkaEstimateParameter;

@DisplayName("Test estimation of alpha(s) based on Minka")
public class MinkaEstimateDirMultAlphaTest {

	public static final double DELTA_SMALL = 1e-6;
	public static final double DELTA_VERY_SMALL = 1e-2;
	
	private static final MinkaEstimateParameter DEFAULT_MINKA_PARAMETER = new MinkaEstimateParameter();
	private MinkaEstimateDirMultAlpha estimateAlpha;
	
	public MinkaEstimateDirMultAlphaTest() {
		estimateAlpha = new MinkaEstimateDirMultAlpha(DEFAULT_MINKA_PARAMETER);
	}
	
	/*
	 * Test method
	 */

	/**
	 * Test method for {@link jacusa.estimate.MinkaEstimateDirMultAlpha#maximizeLogLikelihood(DirMultSample, Info, boolean)}.
	 */
	@DisplayName("Should calculate the correct alpha(s) and log likelihood")
	@ParameterizedTest(name = "Test on: {arguments}")
	@MethodSource("testMaximizeLogLikelihood")
	//@Disabled("Missing init values in csv")
	void testMaximizeLogLikelihood(DirMultData dirMultData, double[] initAlpha, double[] expectedAlpha, double expectedLL) {
		final DirMultSample dirMultSample = new FastDirMultSample("TEST", dirMultData, DEFAULT_MINKA_PARAMETER.getMaxIterations());  
		dirMultSample.add(initAlpha, Double.NaN);
		estimateAlpha.maximizeLogLikelihood(dirMultSample, new Info(), false);
		
		final double[] calculatedAlpha = dirMultSample.getAlpha();
		assertEquals(expectedAlpha.length, calculatedAlpha.length);
		for (int i = 0; i < expectedAlpha.length; ++i) {
			assertEquals(expectedAlpha[i], calculatedAlpha[i], DELTA_SMALL);	
		}

		final double calculatedLL = dirMultSample.getLogLikelihood();
		assertEquals(expectedLL, calculatedLL, DELTA_VERY_SMALL);
	}

	/**
	 * Test method for {@link jacusa.estimate.MinkaEstimateDirMultAlpha#getLogLikelihood(double[], DirMultData)}.
	 */
	@DisplayName("Should calculate the correct log likelihood")
	@ParameterizedTest(name = "Test on GetLogLikelihood: {arguments}")
	@MethodSource("testGetLogLikelihood")
	void testGetLogLikelihood(DirMultData dirMultData, double[] alpha, double expectedLL) {
		final double calculatedLL = estimateAlpha.getLogLikelihood(alpha, dirMultData);
		assertEquals(expectedLL, calculatedLL, 0.01);
	}
	
	@Disabled("Must be implemented")
	void testBacktracking() {
		// TODO
	}

	// Method source

	static Iterator<Arguments> testMaximizeLogLikelihood() throws FileNotFoundException {
		final String fileName = "src/test/jacusa/estimate/dataMaximizeLogLikelihood.csv";
		return new DirMultCSVArgumentIterator(fileName);
	}
	
	static Iterator<Arguments> testGetLogLikelihood() throws FileNotFoundException {
		final String fileName = "src/test/jacusa/estimate/dataGetLogLikelihood.csv";
		return new DirMultCSVArgumentIterator(fileName);
	}

}
