package test.jacusa.filter;

import java.util.Collections;
import java.util.stream.Stream;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.provider.Arguments;

import jacusa.filter.Filter;
import jacusa.filter.FilterByRatio;
import jacusa.filter.GenericBaseCallCountFilter;
import lib.data.DataType;
import lib.data.cache.fetcher.DataTypeFetcher;
import lib.data.count.basecall.BaseCallCount;
import lib.data.count.basecall.DefaultBaseCallCount;
import lib.data.has.LibraryType;
import lib.data.result.Result;
import lib.util.Base;
import lib.util.coordinate.CoordinateUtil.STRAND;
import lib.util.coordinate.OneCoordinate;

/**
 * Tests @see jacusa.filter.GenericBaseCallCountFilter#filter(ParallelData)
 */
@TestInstance(Lifecycle.PER_CLASS)
class GenericBaseCallCountFilterTest extends AbstractFilterTest {

	private final char baseCallSep = ',';
	private final BaseCallCount.AbstractParser parser;
	
	private final DataTypeFetcher<BaseCallCount> observedFetcher;
	private final DataTypeFetcher<BaseCallCount> filteredFetcher;
	private final FilterByRatio filterByRatio;
	
	public GenericBaseCallCountFilterTest() {
		parser 			= new DefaultBaseCallCount.Parser(baseCallSep, '*');
		
		observedFetcher = new DataTypeFetcher<>(DataType.create("Observed", BaseCallCount.class));
		filteredFetcher = new DataTypeFetcher<>(DataType.create("Filtered", BaseCallCount.class));
		filterByRatio 	= new FilterByRatio(0.5);
	}

	Stream<Arguments> testFilter() {
		/*
		 * Format:
		 * 1.	Result
		 * 1a.	STRAND
		 * 1b.	String[][](Observed Base Call Counts)
		 * 1c.	String[][](Filtered Base Call Counts)
		 * 1d.	Base(ref. base)
		 * 1e.	LibraryType
		 * 2	boolean(filter yes/no)
		 */
		return Stream.of(

				Arguments.of(
						createTestInstance(),
						new Result.ResultBuilder(
								new OneCoordinate(), 
								Collections.nCopies(1, LibraryType.UNSTRANDED),
								Base.A)
						.with(0, 0, "10,10,0,0", parser, observedFetcher.getDataType())
						.with(0, 0, "0,5,0,0", parser, filteredFetcher.getDataType())
						.build().getParellelData(),
						true),
				
				Arguments.of(
						createTestInstance(),
						new Result.ResultBuilder(
								new OneCoordinate(), 
								Collections.nCopies(1, LibraryType.UNSTRANDED),
								Base.A)
						.with(0, 0, "10,10,0,0", parser, observedFetcher.getDataType())
						.with(0, 0, "0,4,0,0", parser, filteredFetcher.getDataType())
						.build().getParellelData(),
						false),

				Arguments.of(
						createTestInstance(),
						new Result.ResultBuilder(
								new OneCoordinate("contig", 1, 2, STRAND.FORWARD), 
								Collections.nCopies(1, LibraryType.RF_FIRSTSTRAND),
								Base.A)
						.with(0, 0, "10,10,0,0", parser, observedFetcher.getDataType())
						.with(0, 0, "0,5,0,0", parser, filteredFetcher.getDataType())
						.build().getParellelData(),
						true),

				Arguments.of(
						createTestInstance(),
						new Result.ResultBuilder(
								new OneCoordinate("contig", 1, 2, STRAND.FORWARD), 
								Collections.nCopies(1, LibraryType.FR_SECONDSTRAND),
								Base.A)
						.with(0, 0, "10,10,0,0", parser, observedFetcher.getDataType())
						.with(0, 0, "0,4,0,0", parser, filteredFetcher.getDataType())
						.build().getParellelData(),
						false),

				Arguments.of(
						createTestInstance(),
						new Result.ResultBuilder(
								new OneCoordinate("contig", 1, 2, STRAND.FORWARD), 
								Collections.nCopies(1, LibraryType.RF_FIRSTSTRAND),
								Base.C)
						.with(0, 0, "10,10,0,0", parser, observedFetcher.getDataType())
						.with(0, 0, "0,5,0,0", parser, filteredFetcher.getDataType())
						.build().getParellelData(),
						false),

				Arguments.of(
						createTestInstance(),
						new Result.ResultBuilder(
								new OneCoordinate("contig", 1, 2, STRAND.FORWARD), 
								Collections.nCopies(1, LibraryType.FR_SECONDSTRAND),
								Base.C)
						.with(0, 0, "10,10,0,0", parser, observedFetcher.getDataType())
						.with(0, 0, "0,4,0,0", parser, filteredFetcher.getDataType())
						.build().getParellelData(),
						false),

				Arguments.of(
						createTestInstance(),
						new Result.ResultBuilder(
								new OneCoordinate(), 
								Collections.nCopies(2, LibraryType.UNSTRANDED),
								Base.C)
						.with(0, 0, "10,10,0,0", parser, observedFetcher.getDataType())
						.with(0, 0, "0,5,0,0", parser, filteredFetcher.getDataType())
						.with(1, 0, "10,10,0,0", parser, observedFetcher.getDataType())
						.with(1, 0, "0,5,0,0", parser, filteredFetcher.getDataType())
						.build().getParellelData(),
						true),

				Arguments.of(
						createTestInstance(),
						new Result.ResultBuilder(
								new OneCoordinate(), 
								Collections.nCopies(2, LibraryType.UNSTRANDED),
								Base.A)
						.with(0, 0, "10,10,0,0", parser, observedFetcher.getDataType())
						.with(0, 0, "0,4,0,0", parser, filteredFetcher.getDataType())
						.with(1, 0, "10,10,0,0", parser, observedFetcher.getDataType())
						.with(1, 0, "0,4,0,0", parser, filteredFetcher.getDataType())
						.build().getParellelData(),
						false) );
	}

	Filter createTestInstance() {
		final int overhang = 0;
		final GenericBaseCallCountFilter genericBaseCallCountFilter = new GenericBaseCallCountFilter(
				' ', observedFetcher, filteredFetcher, overhang, filterByRatio );

		return new AbstractFilterWrapper<GenericBaseCallCountFilter>(genericBaseCallCountFilter) {
			
			@Override
			public String toString() {
				return "overhang: " + overhang;
			}

		};
	}
	
}
