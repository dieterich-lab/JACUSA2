package test.jacusa.filter.homopolymer;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import jacusa.filter.homopolymer.HomopolymerReadFilterCache;
import lib.data.DataType;
import lib.data.DataTypeContainer;
import lib.data.DataTypeContainer.AbstractBuilderFactory;
import lib.data.cache.container.CacheContainer;
import lib.data.cache.fetcher.DefaultFilteredDataFetcher;
import lib.data.cache.fetcher.FilteredDataFetcher;
import lib.data.cache.record.RecordWrapperProcessor;
import lib.data.filter.BooleanWrapperFilteredData;
import lib.data.filter.BooleanWrapper;
import lib.data.has.LibraryType;
import lib.util.coordinate.Coordinate;

/**
 * Test @see test.jacusa.filter.homopolymer.HomopolymerReadFilterCache
 */
class HomopolymerReadFilterCacheTest implements RecordWrapperProcessorTest {
	
	private final char c;
	
	private final AbstractBuilderFactory builderFactory;
	private final FilteredDataFetcher<BooleanWrapperFilteredData, BooleanWrapper> filteredDataFetcher;

	private final RecordWrapperSimulator simulator;

	private int minHomopolymerLength;
	
	private CacheContainer testInstanceContainer;
	
	private List<String> expectedWindowStr;
	
	public HomopolymerReadFilterCacheTest() {
		c 					= 'Y';
		
		builderFactory 		= new DataTypeContainer.DefaultBuilderFactory();
		filteredDataFetcher = new DefaultFilteredDataFetcher<>(DataType.F_BOOLEAN);
		
		simulator 			= new RecordWrapperSimulator();
	}
	
	// TODO
	/*
	 * sequence here or somewhere else -> here!, contig = homopolymers
	 * how to generate reads? -> by CIGAR and MD! 
	 */
	
	@ParameterizedTest(name = "Lib.: {0}, Seq.: {1}, readLength {2}, minLength {3}, and windowSize {4} should be {5}")
	@CsvSource(delimiter = ' ', value = {
			"UNSTRANDED ACGTACGT 3 2 4 00000000",
			"UNSTRANDED ACGAACGT 3 2 4 00011000",
			"RF_FIRSTSTRAND ACGAACGT 3 2 8 00011000",
			"FR_SECONDSTRAND ACGAACGT 3 3 8 00000000"
	})
	void testAddRecordWrapper(
			LibraryType libraryType,
			String refSeq,
			int readLength,
			int minLength,
			int activeWindowSize,
			String expectedStr) {

		// random name
		final String contig = "contig";
		this.minHomopolymerLength 		= minLength;
		
		// update read simulator
		simulator.update(activeWindowSize, libraryType, contig, refSeq);
		// create new test instance and wrap in container
		testInstanceContainer = new CacheContainer.Builder(libraryType, simulator.getShareCache())
				.withCache(createTestInstance())
				.build();
		
		// create expected string
		expectedWindowStr = tokenize(activeWindowSize, expectedStr);

		// simulate records
		simulator.getRecordBuilder().addRecords(false, readLength);

		runTest();
	}
	
	@Override
	public void assertEqual(final int windowIndex, Coordinate current) {
		// create data type container that will store homopolymer info
		final DataTypeContainer container = 
				builderFactory.createBuilder(
						current, 
						getRecordWrapperSimulator().getLibraryType())
				.with(filteredDataFetcher.getDataType())
				.build();
		getTestInstanceContainer().populate(container, current);
		
		final int windowPosition = getRecordWrapperSimulator()
				.getShareCache()
				.getCoordinateController()
				.getCoordinateTranslator().convert2windowPosition(current);
		
		final boolean expected = str2booleanArr(expectedWindowStr.get(windowIndex))[windowPosition];
		final boolean actual = filteredDataFetcher.fetch(container).get(c).getValue();
		assertEquals(
				expected, actual, 
				"Error in window: " + windowIndex + "; " +
						"current: " + current.toString() + "; " +
						"expected: " + expected + "; actual: " + actual + "; ");
	}
	
	@Override
	public RecordWrapperProcessor createTestInstance() {
		return new HomopolymerReadFilterCacheTest(
				c, 
				filteredDataFetcher, 
				minHomopolymerLength, 
				getRecordWrapperSimulator().getShareCache() );
	}
	
	@Override
	public RecordWrapperSimulator getRecordWrapperSimulator() {
		return simulator;
	}
	
	@Override
	public CacheContainer getTestInstanceContainer() {
		return testInstanceContainer;
	}
	
	private List<String> tokenize(final int activeWindowSize, final String s) {
		final List<String> res = new ArrayList<>();

		String tmp = new String(s);
		while (tmp.length() > 0) {
			final int length = tmp.length();
			final int beginIndex = Math.min(length, activeWindowSize);
			res.add(tmp.substring(0, beginIndex));
			tmp = tmp.substring(beginIndex);
		}
		
		return res;
	}

	private boolean[] str2booleanArr(final String s) {
		final boolean[] res = new boolean[s.length()];
		Arrays.fill(res, false);

		int index = s.indexOf("1");
		while (index >= 0) {
			res[index] = true;
			index = s.indexOf("1", index + 1);
		}
		return res;
	}
	
}
