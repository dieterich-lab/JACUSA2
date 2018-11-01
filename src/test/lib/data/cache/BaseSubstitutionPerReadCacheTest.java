package test.lib.data.cache;

import java.util.Arrays;
import java.util.TreeSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;



import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.util.CloseableIterator;
import lib.cli.options.has.HasReadSubstitution.BaseSubstitution;
import lib.data.DataType;
import lib.data.DataTypeContainer;
import lib.data.DataTypeContainer.DefaultBuilderFactory;
import lib.data.adder.IncrementAdder;
import lib.data.adder.basecall.DefaultBaseCallAdder;
import lib.data.cache.fetcher.basecall.BaseCallCountExtractor;
import lib.data.cache.readsubstitution.AbstractReadSubstitutionCache;
import lib.data.cache.readsubstitution.StrandedReadSubstitutionCache;
import lib.data.cache.readsubstitution.UnstrandedReadSubstitutionCache;
import lib.data.cache.region.isvalid.MinBASQBaseCallValidator;
import lib.data.has.LibraryType;
import lib.util.coordinate.Coordinate;
import test.jacusa.filter.cache.AbstractRecordCacheTest;
import test.utlis.SAMRecordIterator;

public class BaseSubstitutionPerReadCacheTest extends AbstractRecordCacheTest {
	
	private final TreeSet<BaseSubstitution> baseSubstitutions;
	private final MinBASQBaseCallValidator validator;
	
	public BaseSubstitutionPerReadCacheTest() {
		baseSubstitutions = new TreeSet<>(Arrays.asList(BaseSubstitution.CtoT));
		validator = new MinBASQBaseCallValidator((byte)30);
	}

	@BeforeEach
	public void beforeEach() {
		super.beforeEach();
	}
	
	/**
	 * Test method for {@link jacusa.filter.cache.HomopolymerRecordFilterCache#processRecordWrapper(lib.data.builder.recordwrapper.SAMRecordWrapper)}.
	 */
	@ParameterizedTest(name = "Seq.: {0}, length {1}, lib. {2}, and window {3}")
	@CsvSource(delimiter = ' ', value = {
			// "ACGTACGT 3 UNSTRANDED 4 00000000",
			// "ACGTACGT 3 UNSTRANDED 2 00000000",
			"ACGTACGT 3 RF_FIRSTSTRAND 4 00000000",
			"ACGTACGT 3 FR_SECONDSTRAND 4 00000000"		
	})
	void testAddRecordWrapper(
			String refSeq,
			int readLength,
			LibraryType libraryType,
			int activeWindowSize,
			String expectedBaseStr) {

		// add();
		
		// set sequences...
		final String contig = "contig";
		update(activeWindowSize, libraryType, contig, refSeq);

		// create records		
		getRecordBuilder().addRecords(false, readLength);
		
		test();
	}
	
	protected AbstractReadSubstitutionCache createTestInstance() {
		final IncrementAdder[] substBccAdders = 
				baseSubstitutions.stream()
				.map(
						bs -> new DefaultBaseCallAdder(
								getShareCache(), 
								new BaseCallCountExtractor(bs, DataType.BASE_SUBST.getFetcher())))
				.toArray(IncrementAdder[]::new);
		
		switch (getLibraryType()) {
		case RF_FIRSTSTRAND:
		case FR_SECONDSTRAND:
			return new StrandedReadSubstitutionCache(
					getShareCache(), 
					validator, 
					baseSubstitutions,
					substBccAdders);

		case UNSTRANDED:
			return new UnstrandedReadSubstitutionCache(
					getShareCache(), 
					validator, 
					baseSubstitutions,
					substBccAdders);
			
		default:
			throw new IllegalArgumentException("Unsupported library type: " + getLibraryType().toString());
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
		/*
		final int windowPosition = getWindowPosition(windowIndex, current);
		
		assertEquals(
				expectedArrestBcc.get(windowPosition), data.getBaseSubstitutionCount(), 
				"For arrest, window: " + windowIndex + " and coordinate: " + current.toString() );
				*/
	}
	
}
