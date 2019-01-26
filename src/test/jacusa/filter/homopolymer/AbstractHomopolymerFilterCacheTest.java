package test.jacusa.filter.homopolymer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.provider.Arguments;

import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.util.StringUtil;
import jacusa.filter.homopolymer.AbstractHomopolymerFilterCache;
import lib.data.DataType;
import lib.data.DataTypeContainer;
import lib.data.cache.container.SharedCache;
import lib.data.cache.fetcher.DefaultFilteredDataFetcher;
import lib.data.cache.fetcher.FilteredDataFetcher;
import lib.data.filter.BooleanWrapper;
import lib.data.filter.BooleanWrapperFilteredData;
import lib.data.has.LibraryType;
import lib.util.Base;
import lib.util.coordinate.Coordinate;
import test.utlis.DefaultSAMRecordBuilderStrategy;
import test.utlis.ReferenceSequence;

@TestInstance(Lifecycle.PER_CLASS)
abstract class AbstractHomopolymerFilterCacheTest implements RecordWrapperProcessorTest<String> {

	protected final static String CONTIG = "homopolymerTest";
	private final static char C = 'X';
	
	private final FilteredDataFetcher<BooleanWrapperFilteredData, BooleanWrapper> filteredDataFetcher;

	public AbstractHomopolymerFilterCacheTest() {
		filteredDataFetcher = new DefaultFilteredDataFetcher<>(DataType.F_BOOLEAN);
	}
	
	// TODO add more examples
	// CONTIG -> ACGAACGT - check ReferenceSequence
	Stream<Arguments> testAddRecordWrapper() {
		return Stream.of(
				
				testAddRecordWrapper(
						LibraryType.UNSTRANDED, 
						4,
						new SAMRecordBuilder()
						.withStrategy(CONTIG, new DefaultSAMRecordBuilderStrategy(3))
						.getRecords(),
						3,
						new StringBuilder(),
						Arrays.asList("0000", "0000")),
				
				testAddRecordWrapper(
						LibraryType.UNSTRANDED, 
						4,
						new SAMRecordBuilder()
						.withStrategy(CONTIG, new DefaultSAMRecordBuilderStrategy(3))
						.getRecords(),
						2,
						new StringBuilder(),
						Arrays.asList("0001", "1000")),

				testAddRecordWrapper(
						LibraryType.RF_FIRSTSTRAND, 
						4,
						new SAMRecordBuilder()
						.withStrategy(CONTIG, new DefaultSAMRecordBuilderStrategy(3))
						.getRecords(),
						2,
						new StringBuilder(),
						Arrays.asList("0001", "1000")),
				
				testAddRecordWrapper(
						LibraryType.FR_SECONDSTRAND, 
						4,
						new SAMRecordBuilder()
						.withStrategy(CONTIG, new DefaultSAMRecordBuilderStrategy(3))
						.getRecords(),
						2,
						new StringBuilder(),
						Arrays.asList("0001", "1000")),

				testAddRecordWrapper(
						LibraryType.UNSTRANDED, 
						8,
						new SAMRecordBuilder()
						.withStrategy(CONTIG, new DefaultSAMRecordBuilderStrategy(3))
						.getRecords(),
						2,
						new StringBuilder(),
						Arrays.asList("00011000"))
				);
	}

	Arguments testAddRecordWrapper(
			final LibraryType libraryType,
			final int activeWindowSize,
			final Collection<SAMRecord> records,
			final int minHomopolymerLength,
			final StringBuilder infoBuilder,
			final List<String> expected) {
		
		infoBuilder.append("activeWindowSize=").append(activeWindowSize).append(", ")
			.append("contig=").append(CONTIG).append(", ")
			.append("expected=").append(StringUtil.join("", expected));
		
		final SharedCache sharedCache = new SharedCacheBuilder(
				activeWindowSize, libraryType, CONTIG, ReferenceSequence.get())
				.build();
		
		return Arguments.of(
				createTestInstance(minHomopolymerLength, sharedCache),
				libraryType,
				records,
				expected,
				infoBuilder.toString());
	}
	
	@Override
	public DataTypeContainer createDataTypeContainer(
			Coordinate coordinate, LibraryType libraryType, Base refBase) {
		
		// create data type container that will store homopolymer info
		return createDataTypeContainerBuilder(coordinate, libraryType, refBase)
				.with(filteredDataFetcher.getDataType())
				.build();
	}

	@Override
	public void assertEqual(
			final int windowPosition, final Coordinate currentCoordinate, 
			final DataTypeContainer container, final String expectedStr) {
				
		final boolean expected 	= expectedStr.charAt(windowPosition) == '1' ? true : false;
		final boolean actual 	= filteredDataFetcher.fetch(container).get(C).getValue();
		assertEquals(expected, actual, "Error in coord: " + currentCoordinate.toString());
	}
	
	protected FilteredDataFetcher<BooleanWrapperFilteredData, BooleanWrapper> getFetcher() {
		return filteredDataFetcher;
	}
	
	protected char getC() {
		return C;
	}
	
	abstract AbstractHomopolymerFilterCache createTestInstance(
			int minHomopolymerLength, SharedCache sharedCache);
	
		
}
