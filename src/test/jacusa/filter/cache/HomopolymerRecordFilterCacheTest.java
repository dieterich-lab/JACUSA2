package test.jacusa.filter.cache;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import htsjdk.samtools.util.CloseableIterator;
import htsjdk.samtools.SAMRecord;
import jacusa.filter.cache.HomopolymerRecordFilterCache;
import lib.data.DataType;
import lib.data.DataTypeContainer;
import lib.data.DataTypeContainer.AbstractBuilderFactory;
import lib.data.cache.fetcher.DefaultFilteredDataFetcher;
import lib.data.cache.fetcher.FilteredDataFetcher;
import lib.data.cache.record.RecordWrapperProcessor;
import lib.data.filter.BooleanWrapperFilteredData;
import lib.data.filter.BooleanWrapper;
import lib.data.has.LibraryType;
import lib.util.coordinate.Coordinate;
import test.utlis.SAMRecordIterator;

class HomopolymerRecordFilterCacheTest extends AbstractRecordCacheTest {
	
	private final char c;
	
	private final AbstractBuilderFactory builderFactory;
	private final FilteredDataFetcher<BooleanWrapperFilteredData, BooleanWrapper> filteredDataFetcher;
	
	private int minLength;
	private List<String> expectedWindowStr;
	
	public HomopolymerRecordFilterCacheTest() {
		c = 'Y';
		builderFactory = new DataTypeContainer.DefaultBuilderFactory();
		filteredDataFetcher = new DefaultFilteredDataFetcher<>(DataType.F_BOOLEAN);
	}
	
	@BeforeEach
	protected void beforeEach() {
		minLength = -1;
		expectedWindowStr = null;
		super.beforeEach();
	}
	
	/**
	 * Test method for {@link jacusa.filter.cache.HomopolymerRecordFilterCache#process(lib.data.builder.recordwrapper.SAMRecordWrapper)}.
	 */
	@ParameterizedTest(name = "Seq.: {0}, readLength {1}, minLength {2}, and windowSize {3} should be {4}")
	@CsvSource(delimiter = ' ', value = {
			"ACGTACGT 3 2 4 00000000",
			"ACGAACGT 3 2 4 00011000",
			"ACGAACGT 3 2 8 00011000",
			"ACGAACGT 3 3 8 00000000"
	})
	void testAddRecordWrapper(
			String refSeq,
			int readLength,
			int minLength,
			int activeWindowSize,
			String expectedStr) {

		// set sequences...
		final String contig = "contig";
		this.minLength = minLength;
		update(activeWindowSize, LibraryType.UNSTRANDED, contig, refSeq);
		expectedWindowStr = tokenize(activeWindowSize, expectedStr);
		
		// create records		
		getRecordBuilder().addRecords(false, readLength);

		test();
	}

	@Override
	protected void assertEqual(final int windowIndex, Coordinate current) {
		final boolean[] expected = str2booleanArr(expectedWindowStr.get(windowIndex));
		
		final DataTypeContainer container = builderFactory.createBuilder(current, getLibraryType()).build();
		getCacheContainer().populateContainer(container, current);
		
		int window = getShareCache().getCoordinateController().getCoordinateTranslator().convert2windowPosition(current);
		
		assertEquals(
				expected[window],
				filteredDataFetcher.fetch(container).get(c), 
				"Error in window: " + windowIndex + "; current: " + current.toString());
		
	
	}

	@Override
	protected RecordWrapperProcessor createTestInstance() {
		return new HomopolymerRecordFilterCache(
				c, filteredDataFetcher, minLength, getShareCache());
	}
	
	@Override
	protected CloseableIterator<SAMRecord> createIterator(String contig, int start, int end) {
		return new SAMRecordIterator(contig, start, end, getRecordBuilder().getRecords());
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
