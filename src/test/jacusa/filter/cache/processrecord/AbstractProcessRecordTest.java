package test.jacusa.filter.cache.processrecord;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.provider.Arguments;

import jacusa.filter.cache.processrecord.ProcessRecord;
import jacusa.filter.homopolymer.RecordProcessDataCache;
import lib.data.DataType;
import lib.data.DataTypeContainer;
import lib.data.adder.IncrementAdder;
import lib.data.adder.basecall.ArrayBaseCallAdder;
import lib.data.adder.region.ValidatedRegionDataCache;
import lib.data.cache.container.SharedCache;
import lib.data.cache.fetcher.DefaultFilteredDataFetcher;
import lib.data.cache.fetcher.FilteredDataFetcher;
import lib.data.cache.fetcher.SpecificFilteredDataFetcher;
import lib.data.cache.record.RecordWrapperProcessor;
import lib.data.cache.region.RegionDataCache;
import lib.data.cache.region.UniqueTraverse;
import lib.data.cache.region.isvalid.BaseCallValidator;
import lib.data.count.basecall.BaseCallCount;
import lib.data.count.basecall.DefaultBaseCallCount;
import lib.data.filter.BaseCallCountFilteredData;
import lib.data.has.LibraryType;
import lib.util.Base;
import lib.util.coordinate.Coordinate;
import test.jacusa.filter.homopolymer.RecordWrapperProcessorTest;
import test.jacusa.filter.homopolymer.SAMRecordBuilder;
import test.jacusa.filter.homopolymer.SharedCacheBuilder;
import test.utlis.ReferenceSequence;

@TestInstance(Lifecycle.PER_CLASS)
abstract class AbstractProcessRecordTest 
implements RecordWrapperProcessorTest<String> {

	// recordBuilder.setUseNmFlag(true);
	private final static char C = 'X';

	public static final String CONTIG = "processRecordTest";
	
	private final FilteredDataFetcher<BaseCallCountFilteredData, BaseCallCount> filteredDataFetcher;

	public AbstractProcessRecordTest() {
		filteredDataFetcher = new DefaultFilteredDataFetcher<>(DataType.F_BCC);
	}

	abstract Stream<Arguments> testAddRecordWrapper();
	
	@Override
	public DataTypeContainer createDataTypeContainer(
			Coordinate coordinate, LibraryType libraryType, Base refBase) {
		
		// create data type container that will store filtered info
		return createDataTypeContainerBuilder(coordinate, libraryType, refBase)
				.with(
						filteredDataFetcher.getDataType(), 
						new BaseCallCountFilteredData().add(getC(), new DefaultBaseCallCount()))
				.build();
	}
	
	protected FilteredDataFetcher<BaseCallCountFilteredData, BaseCallCount> getFetcher() {
		return filteredDataFetcher;
	}
	
	protected char getC() {
		return C;
	}
	
	abstract List<ProcessRecord> createTestInstances(int distance, RegionDataCache regionDataCache);
	
	RecordWrapperProcessor createRecordWrapperProcessor(final int distance, final SharedCache sharedCache) {
		final List<IncrementAdder> adder 	= new ArrayList<IncrementAdder>();
		final IncrementAdder baseCallAdder 	= new ArrayBaseCallAdder(
				new SpecificFilteredDataFetcher<>(getC(), getFetcher()), sharedCache);
		adder.add(baseCallAdder);
		
		final List<BaseCallValidator> validator = new ArrayList<BaseCallValidator>();
		
		final ValidatedRegionDataCache regionDataCache = 
				new ValidatedRegionDataCache(adder, validator, sharedCache);
		
		final UniqueTraverse uniqueTraverse = new UniqueTraverse(regionDataCache);
		return new RecordProcessDataCache(
				uniqueTraverse, 
				createTestInstances(distance, uniqueTraverse));		
	}

	protected List<String> tokern(final int activeWindowSize, final String expected) {
		final List<String> token = new ArrayList<String>();
		
		String tmp = expected;
		while (tmp.length() > 0) {
			final int length = Math.min(activeWindowSize, tmp.length());
			token.add(tmp.substring(0, length));
			if (length == tmp.length()) {
				tmp = "";
			} else {
				tmp = tmp.substring(length);
			}
		}
		
		return token;
	}
	
	@Override
	public void assertEqual(
			int windowPosition, Coordinate currentCoordinate, 
			DataTypeContainer container,
			String expectedStr) {
		
		final BaseCallCount expected 	= createBaseCallCount(expectedStr.charAt(windowPosition));
		final BaseCallCount actual 		= filteredDataFetcher.fetch(container).get(C);
		assertEquals(expected, actual, "Error in coord: " + currentCoordinate.toString());
	}
	
	BaseCallCount createBaseCallCount(final char b) {
		final BaseCallCount bcc = new DefaultBaseCallCount();
		if (b == '*') {
			return bcc;
		}
		final Base base = Base.valueOf(b);
		bcc.increment(base);
		return bcc;
	}

	Arguments createArguments(
			final int activeWindowSize,
			final LibraryType libraryType,
			final int distance, 
			final int refStart,
			final boolean negativeStrand,
			final String cigarStr, 
			final String MD,
			final List<String> expected,
			final StringBuilder infoBuilder) {
		
		final SharedCache sharedCache = new SharedCacheBuilder(
				activeWindowSize, libraryType, 
				CONTIG, ReferenceSequence.get())
				.build();
		
		infoBuilder
		.append("ActiveWindowSize: ").append(activeWindowSize).append(", ")
		.append("Read: ")
		.append(refStart).append(':')
		.append(negativeStrand ? '-' : '+').append(':')
		.append(cigarStr); // .append(", ");	
			
						
		return Arguments.of(
				createRecordWrapperProcessor(distance, sharedCache),
				libraryType,
				new SAMRecordBuilder()
					.withSERead(CONTIG, refStart, negativeStrand, cigarStr, MD)
					.getRecords(),
				expected,
				infoBuilder.toString());
	}
	
}
