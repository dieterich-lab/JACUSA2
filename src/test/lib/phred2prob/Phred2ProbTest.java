package test.lib.phred2prob;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;
import java.util.Iterator;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.MethodSource;

import lib.data.count.PileupCount;
import lib.data.count.basecallquality.BaseCallQualityCount;
import lib.data.count.basecallquality.MapBaseCallQualityCount;
import lib.phred2prob.Phred2Prob;
import lib.util.Base;
import test.utlis.AbstractArgumentIterator;

/**
 * Tests @see lib.phred2prob.Phred2Prob
 */
@TestInstance(Lifecycle.PER_CLASS)
public class Phred2ProbTest {

	private static final double DELTA = 1e-6;
	private static final String PATH = "src/test/lib/phred2prob/"; 
	
	private Phred2Prob testInstance;
	
	@BeforeAll
	public void beforeAll() {
		testInstance = Phred2Prob.getInstance(Base.validValues().length);
	}
	
	@ParameterizedTest(name = "Given qual {0} expect errorP {1}")
	@CsvFileSource(resources = { "dataConvert2errorP.csv" })
	void testConvert2errorP(byte qual, double expected) {
		final double actual = testInstance.convert2errorP(qual);
		assertEquals(expected, actual, DELTA);
	}

	@ParameterizedTest(name = "Given qual {0} expect P {1}")
	@CsvFileSource(resources = { "dataConvert2P.csv" })
	void testConvert2P(byte qual, double expected) {
		final double actual = testInstance.convert2P(qual);
		assertEquals(expected, actual, DELTA);
	}

	@ParameterizedTest(name = "For bases {0} and pileupCount {1} calculate colSumCount {2}")
	@MethodSource("testColSumCount")
	void testColSumCount(Base[] bases, PileupCount pileupCount, double[] expected) {
		final double[] actual = testInstance.colSumCount(bases, pileupCount);
		assertArrayEquals(expected, actual, DELTA);
	}

	@ParameterizedTest(name = "For bases {0} and pileupCount {1} calculate colSumProb {2}")
	@MethodSource("testColSumProb")
	void testColSumProb(Base[] bases, PileupCount pileupCount, double[] expected) {
		final double[] actual = testInstance.colSumProb(bases, pileupCount);
		assertArrayEquals(expected, actual, DELTA);
	}

	@ParameterizedTest(name = "For bases {0} and pileupCount {1} calculate colSumErrorProb {2}")
	@MethodSource("testColSumErrorProb")
	void testColSumErrorProb(Base[] bases, PileupCount pileupCount, double[] expected) {
		final double[] actual = testInstance.colSumErrorProb(bases, pileupCount);
		assertArrayEquals(expected, actual, DELTA);
	}

	@ParameterizedTest(name = "For bases {0} and pileupCount {1} calculate colMeanErrorProb {2}")
	@MethodSource("testColMeanErrorProb")
	void testColMeanErrorProb(Base[] bases, PileupCount pileupCount, double[] expected) {
		final double[] actual = testInstance.colMeanErrorProb(bases, pileupCount);
		assertArrayEquals(expected, actual, DELTA);
	}

	@ParameterizedTest(name = "For bases {0} and pileupCount {1} calculate colMeanProb {2}")
	@MethodSource("testColMeanProb")
	void testColMeanProb(Base[] bases, PileupCount pileupCount, double[] expected) {
		final double[] actual = testInstance.colMeanProb(bases, pileupCount);
		assertArrayEquals(expected, actual, DELTA);
	}

	/*
	 * Method Source
	 */

	public Iterator<Arguments> testColSumCount() throws FileNotFoundException {
		final String fileName = PATH + "dataColSumCount.csv";
		return new Phred2probCSVArgumentIterator(fileName);
	}
	
	public Iterator<Arguments> testColSumProb() throws FileNotFoundException {
		final String fileName = PATH + "dataColSumProb.csv";
		return new Phred2probCSVArgumentIterator(fileName);
	}
	
	public Iterator<Arguments> testColSumErrorProb() throws FileNotFoundException {
		final String fileName = PATH + "dataColSumErrorProb.csv";
		return new Phred2probCSVArgumentIterator(fileName);
	}
	
	public Iterator<Arguments> testColMeanErrorProb() throws FileNotFoundException {
		final String fileName = PATH + "dataColMeanErrorProb.csv";
		return new Phred2probCSVArgumentIterator(fileName);
	}
	
	public Iterator<Arguments> testColMeanProb() throws FileNotFoundException {
		final String fileName = PATH + "dataColMeanProb.csv";
		return new Phred2probCSVArgumentIterator(fileName);
	}

	/*
	 * Helper
	 */
	
	private class Phred2probCSVArgumentIterator extends AbstractArgumentIterator {
		
		private String SEP = ",";
		private String SEP2 = ";";
		
		public Phred2probCSVArgumentIterator(final String fileName) throws FileNotFoundException {
			super(fileName, "\t");
		}

		@Override
		protected Arguments createArguments(String[] cols) {
			final Base[] bases 				= buildBases(cols[0]);
			final BaseCallQualityCount bcqc = buildBaseCallQualityCount(cols[1]);
			final PileupCount pileupCount	= new PileupCount(bcqc);
			final double[] expected 		= buildExpected(cols[2]);
			
			return Arguments.of(bases, pileupCount, expected);
		}
		
		private Base[] buildBases(final String basesStr) {
			final String basesStrArr[] = basesStr.split(SEP);
			final Base[] bases = new Base[basesStrArr.length];
			for (int i = 0; i < basesStrArr.length; ++i) {
				if (basesStrArr[i].length() > 1) {
					throw new IllegalArgumentException("Illegal base length");
				}
				bases[i] = Base.valueOf(basesStrArr[i]);
			}
			return bases;
		}
				
		private BaseCallQualityCount buildBaseCallQualityCount(final String bcqcStr) {
			final String[] bcqcArrStr = bcqcStr.split(SEP);
			if (bcqcArrStr.length != Base.validValues().length) {
				throw new IllegalArgumentException("Size of expected != valid bases");
			}
			final BaseCallQualityCount bcqc = new MapBaseCallQualityCount();
			for (int baseIndex = 0; baseIndex < bcqcArrStr.length; ++baseIndex) {
				final Base base = Base.valueOf(baseIndex);
				bcqcArrStr[baseIndex] = bcqcArrStr[baseIndex].replaceAll("NA", "");
				
				if (! bcqcArrStr[baseIndex].isEmpty()) {
					final String[] baseCallQualArrStr = bcqcArrStr[baseIndex].split(SEP2);
					for (int i = 0; i < baseCallQualArrStr.length; ++i) {
						final byte baseQual = Byte.parseByte(baseCallQualArrStr[i]);
						bcqc.increment(base, baseQual);
					}
				}
			}
			return bcqc;
		}
		
		private double[] buildExpected(final String s) {
			final String[] e = s.split(SEP);
			if (e.length != Base.validValues().length) {
				throw new IllegalArgumentException("Size of expected != valid bases");
			}
			final double[] expected = new double[e.length];
			for (int i = 0; i < e.length; ++i) {
				expected[i] = Double.parseDouble(e[i]);
			}
			return expected;
		}

	}
	
}
