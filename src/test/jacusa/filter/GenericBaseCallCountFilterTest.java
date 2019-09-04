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
import lib.data.ParallelData;
import lib.data.count.basecall.BaseCallCount;
import lib.data.count.basecall.DefaultBCC;
import lib.data.fetcher.DataTypeFetcher;
import lib.data.result.Result;
import lib.util.Base;
import lib.util.LibraryType;
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

	private int testNumber;
	
	public GenericBaseCallCountFilterTest() {
		parser 			= new DefaultBCC.Parser(baseCallSep, '*');
		
		observedFetcher = new DataTypeFetcher<>(DataType.retrieve("Observed", BaseCallCount.class));
		filteredFetcher = new DataTypeFetcher<>(DataType.retrieve("Filtered", BaseCallCount.class));
		filterByRatio 	= new FilterByRatio(0.5);
		
		testNumber 		= 0;
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

				createArguments(
						0,
						new Result.ResultBuilder(
								new OneCoordinate(), 
								Collections.nCopies(1, LibraryType.UNSTRANDED),
								Base.A)
						.with(0, 0, "10,10,0,0", parser, observedFetcher.getDataType())
						.with(0, 0, "0,5,0,0", parser, filteredFetcher.getDataType())
						.build().getParellelData(),
						true),
				
				createArguments(
						0,
						new Result.ResultBuilder(
								new OneCoordinate(), 
								Collections.nCopies(1, LibraryType.UNSTRANDED),
								Base.A)
						.with(0, 0, "10,10,0,0", parser, observedFetcher.getDataType())
						.with(0, 0, "0,4,0,0", parser, filteredFetcher.getDataType())
						.build().getParellelData(),
						false),

				createArguments(
						0,
						new Result.ResultBuilder(
								new OneCoordinate("contig", 1, 2, STRAND.FORWARD), 
								Collections.nCopies(1, LibraryType.RF_FIRSTSTRAND),
								Base.A)
						.with(0, 0, "10,10,0,0", parser, observedFetcher.getDataType())
						.with(0, 0, "0,5,0,0", parser, filteredFetcher.getDataType())
						.build().getParellelData(),
						true),

				createArguments(
						0,
						new Result.ResultBuilder(
								new OneCoordinate("contig", 1, 2, STRAND.FORWARD), 
								Collections.nCopies(1, LibraryType.FR_SECONDSTRAND),
								Base.A)
						.with(0, 0, "10,10,0,0", parser, observedFetcher.getDataType())
						.with(0, 0, "0,4,0,0", parser, filteredFetcher.getDataType())
						.build().getParellelData(),
						false),

				createArguments(
						0,
						new Result.ResultBuilder(
								new OneCoordinate("contig", 1, 2, STRAND.FORWARD), 
								Collections.nCopies(1, LibraryType.RF_FIRSTSTRAND),
								Base.C)
						.with(0, 0, "10,10,0,0", parser, observedFetcher.getDataType())
						.with(0, 0, "0,5,0,0", parser, filteredFetcher.getDataType())
						.build().getParellelData(),
						false),

				createArguments(
						0,
						new Result.ResultBuilder(
								new OneCoordinate("contig", 1, 2, STRAND.FORWARD), 
								Collections.nCopies(1, LibraryType.FR_SECONDSTRAND),
								Base.C)
						.with(0, 0, "10,10,0,0", parser, observedFetcher.getDataType())
						.with(0, 0, "0,4,0,0", parser, filteredFetcher.getDataType())
						.build().getParellelData(),
						false),

				createArguments(
						0,
						new Result.ResultBuilder(
								new OneCoordinate(), 
								Collections.nCopies(2, LibraryType.UNSTRANDED),
								Base.A)
						.with(0, 0, "10,0,0,0", parser, observedFetcher.getDataType())
						.with(0, 0, "10,0,0,0", parser, filteredFetcher.getDataType())
						.with(1, 0, "5,5,0,0", parser, observedFetcher.getDataType())
						.with(1, 0, "5,5,0,0", parser, filteredFetcher.getDataType())
						.build().getParellelData(),
						true),

				createArguments(
						0,
						new Result.ResultBuilder(
								new OneCoordinate(), 
								Collections.nCopies(2, LibraryType.UNSTRANDED),
								Base.A)
						.with(0, 0, "10,0,0,0", parser, observedFetcher.getDataType())
						.with(0, 0, "10,0,0,0", parser, filteredFetcher.getDataType())
						.with(1, 0, "5,5,0,0", parser, observedFetcher.getDataType())
						.with(1, 0, "5,2,0,0", parser, filteredFetcher.getDataType())
						.build().getParellelData(),
						false) );
	}

	Arguments createArguments(
			final int overhang, final ParallelData parallelData, final boolean expected) {

		return Arguments.of(
				createTestInstance(overhang),
				parallelData,
				expected,
				new StringBuilder()
				.append("test: ").append(++testNumber).append("; ")
				.append("parallelData: ").append(parallelData.toString())
				.toString() );
	}
	
	Filter createTestInstance(final int overhang) {
		return new GenericBaseCallCountFilter(
				' ', observedFetcher, filteredFetcher, overhang, filterByRatio );
	}
	
}
