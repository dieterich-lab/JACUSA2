package test.lib.data.cache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;



import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.util.CloseableIterator;
import lib.data.DataTypeContainer;
import lib.data.DataTypeContainer.DefaultBuilderFactory;
import lib.data.cache.container.CacheContainer;
import lib.data.cache.readsubstitution.ReadSubstitutionCache;
import lib.data.has.LibraryType;
import lib.util.coordinate.Coordinate;
import test.jacusa.filter.homopolymer.RecordWrapperProcessorTest;
import test.jacusa.filter.homopolymer.SharedCacheBuilder;
import test.utlis.SAMRecordIterator;

public class BaseSubstitutionPerReadCacheTest implements RecordWrapperProcessorTest {
	
	// private final TreeSet<BaseSubstitution> baseSubstitutions;
	// private final MinBASQBaseCallValidator validator;
	
	private final SharedCacheBuilder simulator;
	
	private CacheContainer testInstanceContainer;
	
	public BaseSubstitutionPerReadCacheTest() {
		// baseSubstitutions = new TreeSet<>(Arrays.asList(BaseSubstitution.CtoT));
		// validator = new MinBASQBaseCallValidator((byte)30);
		
		simulator 			= new SharedCacheBuilder();
	}

	@BeforeEach
	public void beforeEach() {
		// TODO
	}
	
	/**
	 * Test method for {@link jacusa.filter.homopolymer.HomopolymerReadFilterCache#process(lib.data.builder.recordwrapper.SAMRecordWrapper)}.
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
		simulator.update(activeWindowSize, libraryType, contig, refSeq);

		// create records		
		simulator.getRecordBuilder().addRecords(false, readLength);
		
		runTest();
	}
	
	public ReadSubstitutionCache createTestInstance() {
		/*
		final IncrementAdder[] substBccAdders = 
				baseSubstitutions.stream()
				.map(
						bs -> new DefaultBaseCallAdder(
								getShareCache(), 
								new BaseCallCountExtractor(bs, DataType.BASE_SUBST.getFetcher())))
				.toArray(IncrementAdder[]::new);
		*/
		return null; // FIXME
	}
	
	@Override
	public void assertEqual(final int windowIndex, Coordinate current) {
		final DataTypeContainer container = new DefaultBuilderFactory()
				.createBuilder(current, simulator.getLibraryType())
				.build();
		getTestInstanceContainer().populate(container, current);
		/*
		final int windowPosition = getWindowPosition(windowIndex, current);
		
		assertEquals(
				expectedArrestBcc.get(windowPosition), data.getBaseSubstitutionCount(), 
				"For arrest, window: " + windowIndex + " and coordinate: " + current.toString() );
				*/
	}
	
}
