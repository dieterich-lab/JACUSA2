package test.lib.data.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.util.CloseableIterator;
import lib.data.DataTypeContainer;
import lib.data.DataTypeContainer.DefaultBuilderFactory;
import lib.data.cache.arrest.RTarrestDataCache;
import lib.data.count.basecall.ArrayBaseCallCount;
import lib.data.count.basecall.BaseCallCount;
import lib.data.has.LibraryType;
import lib.util.coordinate.Coordinate;
import test.jacusa.filter.cache.AbstractRecordCacheTest;
import test.utlis.SAMRecordIterator;

public class RTarrestTest extends AbstractRecordCacheTest {

	private final BaseCallCount.AbstractParser parser;
	
	private List<BaseCallCount> expectedArrestBcc;
	private List<BaseCallCount> expectedThroughBcc;
	
	public RTarrestTest() {
		parser = new ArrayBaseCallCount.Parser(',', '*');
		
		expectedArrestBcc = new ArrayList<>();
		expectedThroughBcc = new ArrayList<>(); 
	}

	@BeforeEach
	public void beforeEach() {
		expectedArrestBcc.clear();
		expectedThroughBcc.clear();
		super.beforeEach();
	}
	
	/**
	 * Test method for {@link jacusa.filter.cache.HomopolymerRecordFilterCache#processRecordWrapper(lib.data.builder.recordwrapper.SAMRecordWrapper)}.
	 */
	@ParameterizedTest(name = "Seq.: {0}, length {1}, lib. {2}, and window {3}")
	@CsvSource(delimiter = ' ', value = {
			"ACGTACGT 3 UNSTRANDED 4 1,0,0,0;0,1,0,0;0,0,2,0;0,0,0,2;2,0,0,0;0,2,0,0;0,0,1,0;0,0,0,1 0,0,0,0;0,1,0,0;0,0,1,0;0,0,0,1;1,0,0,0;0,1,0,0;0,0,1,0;0,0,0,0",
			"ACGTACGT 3 UNSTRANDED 2 1,0,0,0;0,1,0,0;0,0,2,0;0,0,0,2;2,0,0,0;0,2,0,0;0,0,1,0;0,0,0,1 0,0,0,0;0,1,0,0;0,0,1,0;0,0,0,1;1,0,0,0;0,1,0,0;0,0,1,0;0,0,0,0",
			"ACGTACGT 3 RF_FIRSTSTRAND 4 " + "0,0,0,0;" + "0,0,0,0;" +
											 "0,0,0,0;" + "0,0,0,0;" +
											 "0,0,0,0;" + "0,1,0,0;" +
											 "0,0,0,0;" + "1,0,0,0;" +
											 "0,0,0,0;" + "0,0,0,1;" +
											 "0,0,0,0;" + "0,0,1,0;" +
											 "0,0,0,0;" + "0,1,0,0;" +
											 "0,0,0,0;" + "1,0,0,0" +
											 " " +
											 "0,0,0,0;" + "0,0,0,1;" +
											 "0,0,0,0;" + "0,0,2,0;" +
											 "0,0,0,0;" + "0,2,0,0;" +
											 "0,0,0,0;" + "2,0,0,0;" +
											 "0,0,0,0;" + "0,0,0,2;" +
											 "0,0,0,0;" + "0,0,2,0;" +
											 "0,0,0,0;" + "0,2,0,0;" +
											 "0,0,0,0;" + "0,0,0,0",
			"ACGTACGT 3 FR_SECONDSTRAND 4 " + "1,0,0,0;" + "0,0,0,0;" +
											  "0,1,0,0;" + "0,0,0,0;" +
											  "0,0,1,0;" + "0,0,0,0;" +
											  "0,0,0,1;" + "0,0,0,0;" +
											  "1,0,0,0;" + "0,0,0,0;" +
											  "0,1,0,0;" + "0,0,0,0;" +
											  "0,0,0,0;" + "0,0,0,0;" +
											  "0,0,0,0;" + "0,0,0,0"  +
											  " " +     
											  "0,0,0,0;" + "0,0,0,0;" +
											  "0,1,0,0;" + "0,0,0,0;" +
											  "0,0,2,0;" + "0,0,0,0;" +
											  "0,0,0,2;" + "0,0,0,0;" +
											  "2,0,0,0;" + "0,0,0,0;" +
											  "0,2,0,0;" + "0,0,0,0;" +
											  "0,0,2,0;" + "0,0,0,0;" +
											  "0,0,0,1;" + "0,0,0,0"		
	})
	void testAddRecordWrapper(
			String refSeq,
			int readLength,
			LibraryType libraryType,
			int activeWindowSize,
			String expectedArrestStr,
			String expectedThroughStr) {

		add(expectedArrestStr, expectedArrestBcc);
		add(expectedThroughStr, expectedThroughBcc);
		
		// set sequences...
		final String contig = "contig";
		update(activeWindowSize, libraryType, contig, refSeq);

		// create records		
		getRecordBuilder().addRecords(false, readLength);
		
		test();
	}
	
	protected RTarrestDataCache createTestInstance() {
		/*
		switch (getLibraryType()) {
		case UNSTRANDED:
			return new UNSTRANDED_RTarrestDataCache((byte)0, getShareCache() );
		
		case RF_FIRSTSTRAND:
			return new RF_FIRSTSTRAND_RTarrestDataCache((byte)0, getShareCache() );

		case FR_SECONDSTRAND:
			return new FR_SECONDSTRAND_RTarrestDataCache((byte)0, getShareCache() );
		
		default:
			throw new IllegalArgumentException("Unsupported library type: " + getLibraryType().toString());
		}
		*/
		return null;
	}
	
	private void add(final String s, List<BaseCallCount> bccs) {
		for (final String tmp : s.split(";")) {
			final BaseCallCount bcc = parser.parse(tmp);
			bccs.add(bcc);
		}
	}
	
	@Override
	protected CloseableIterator<SAMRecord> createIterator(String contig, int start, int end) {
		return new SAMRecordIterator(contig, start, end, getRecordBuilder().getRecords());
	}
	
	@Override
	protected void assertEqual(final int windowIndex, Coordinate current) {
		final DataTypeContainer container = new DefaultBuilderFactory()
				.createBuilder(current, getLibraryType())
				.build();
		getCacheContainer().populateContainer(container, current);
		final int windowPosition = getWindowPosition(windowIndex, current);
		assertEquals(
				expectedArrestBcc.get(windowPosition), container.getArrestBaseCallCount(), 
				"For arrest, window: " + windowIndex + " and coordinate: " + current.toString() );
		/*
		assertEquals(
				expectedThroughBcc.get(windowPosition), data.getThroughBaseCallCount(), 
				"For through, window: " + windowIndex + " and coordinate: " + current.toString() );
				*/
	}

	/*
	 *  	|ACG
			| CGT
			|  GTA
			|   TAC
			|    ACG
			|     CGT
			A11222211
			T01111110
			|01234567
			|12345678
			
			|ACG
			| CGT
			|  GTA
			|   TAC
			|    ACG
			|     CGT
			A00111111
			T12222220
			|01234567
			|12345678
			
	 */
	
}
