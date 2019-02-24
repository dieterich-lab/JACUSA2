package test.jacusa.filter;

import java.util.Collections;
import java.util.stream.Stream;

import org.junit.jupiter.params.provider.Arguments;

import jacusa.filter.Filter;
import jacusa.filter.HomopolymerFilter;
import lib.data.DataType;
import lib.data.ParallelData;
import lib.data.fetcher.DefaultFilteredDataFetcher;
import lib.data.fetcher.FilteredDataFetcher;
import lib.data.fetcher.SpecificFilteredDataFetcher;
import lib.data.filter.BooleanData;
import lib.data.filter.BooleanFilteredData;
import lib.data.result.Result;
import lib.util.LibraryType;
import lib.util.Parser;
import lib.util.coordinate.OneCoordinate;

/**
 * Tests @see jacusa.filter.HomopolymerFilter#filter(lib.data.ParallelData)
 */
class HomopolymerFilterTest extends AbstractFilterTest {

	private final char SEP = ':';
	
	private final char c;
	
	private final Parser<BooleanFilteredData> parser; 
	private final FilteredDataFetcher<BooleanFilteredData, BooleanData> fetcher;
	
	public HomopolymerFilterTest() {
		c 		= 'X';
		
		parser	= new BooleanFilteredData.Parser(',', SEP);
		fetcher = new DefaultFilteredDataFetcher<>(
				DataType.retrieve("Filtered boolean", BooleanFilteredData.class) );
	}
	
	@Override
	Stream<Arguments> testFilter() {
		return Stream.of(

				createArguments(
						0,
						new Result.ResultBuilder(
								new OneCoordinate(), Collections.nCopies(2, LibraryType.UNSTRANDED))
						.with(0, 0, h(false), parser, fetcher.getDataType())
						.with(1, 0, h(false), parser, fetcher.getDataType())
						.build().getParellelData(),
						false),

				createArguments(
						0,
						new Result.ResultBuilder(
								new OneCoordinate(), Collections.nCopies(2, LibraryType.UNSTRANDED))
						.with(0, 0, h(true), parser, fetcher.getDataType())
						.with(1, 0, h(false), parser, fetcher.getDataType())
						.build().getParellelData(),
						true),
				
				createArguments(
						0,
						new Result.ResultBuilder(
								new OneCoordinate(), Collections.nCopies(2, LibraryType.UNSTRANDED))
						.with(0, 0, h(false), parser, fetcher.getDataType())
						.with(1, 0, h(true), parser, fetcher.getDataType())
						.build().getParellelData(),
						true),

				createArguments(
						0,
						new Result.ResultBuilder(
								new OneCoordinate(), Collections.nCopies(2, LibraryType.UNSTRANDED))
						.with(0, 0, h(true), parser, fetcher.getDataType())
						.with(1, 0, h(true), parser, fetcher.getDataType())
						.build().getParellelData(),
						true),
				
				createArguments(
						0,
						new Result.ResultBuilder(
								new OneCoordinate(), Collections.nCopies(2, LibraryType.UNSTRANDED))
						.with(0, 0, h(false), parser, fetcher.getDataType())
						.with(0, 1, h(false), parser, fetcher.getDataType())
						.with(1, 0, h(false), parser, fetcher.getDataType())
						.with(1, 1, h(false), parser, fetcher.getDataType())
						.build().getParellelData(),
						false),
				
				createArguments(
						0,
						new Result.ResultBuilder(
								new OneCoordinate(), Collections.nCopies(2, LibraryType.UNSTRANDED))
						.with(0, 0, h(true), parser, fetcher.getDataType())
						.with(0, 1, h(false), parser, fetcher.getDataType())
						.with(1, 0, h(false), parser, fetcher.getDataType())
						.with(1, 1, h(false), parser, fetcher.getDataType())
						.build().getParellelData(),
						true),
				
				createArguments(
						0,
						new Result.ResultBuilder(
								new OneCoordinate(), Collections.nCopies(2, LibraryType.UNSTRANDED))
						.with(0, 0, h(false), parser, fetcher.getDataType())
						.with(0, 1, h(false), parser, fetcher.getDataType())
						.with(1, 0, h(true), parser, fetcher.getDataType())
						.with(1, 1, h(false), parser, fetcher.getDataType())
						.build().getParellelData(),
						true) );
	}

	String h(final boolean b) {
		final StringBuilder sb = new StringBuilder();
		sb.append(c);
		sb.append(SEP);
		sb.append(Boolean.toString(b));
		return sb.toString();
	}
	
	Arguments createArguments(
			final int overhang, final ParallelData parallelData, final boolean expected) {
		
		return Arguments.of(
				createTestInstance(overhang),
				parallelData,
				expected,
				"conditionIndex: " + Integer.toString(overhang));
	}
	
	Filter createTestInstance(final int overhang) {
		return new HomopolymerFilter(
				c, overhang, new SpecificFilteredDataFetcher<>(c, fetcher) );
	}
	
}
