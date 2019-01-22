package test.jacusa.filter;

import java.util.Collections;
import java.util.stream.Stream;

import org.junit.jupiter.params.provider.Arguments;

import jacusa.filter.Filter;
import jacusa.filter.HomopolymerFilter;
import lib.data.DataType;
import lib.data.cache.fetcher.DefaultFilteredDataFetcher;
import lib.data.cache.fetcher.FilteredDataFetcher;
import lib.data.cache.fetcher.SpecificFilteredDataFetcher;
import lib.data.filter.BooleanWrapper;
import lib.data.filter.BooleanWrapperFilteredData;
import lib.data.has.LibraryType;
import lib.data.result.Result;
import lib.util.Parser;
import lib.util.coordinate.Coordinate;

/**
 * Tests @see jacusa.filter.HomopolymerFilter#filter(lib.data.ParallelData)
 */
class HomopolymerFilterTest extends AbstractFilterTest {

	private final char SEP = ':';
	
	private final char c;
	
	private final Parser<BooleanWrapperFilteredData> parser; 
	private final FilteredDataFetcher<BooleanWrapperFilteredData, BooleanWrapper> fetcher;
	
	public HomopolymerFilterTest() {
		c 		= 'X';
		
		parser	= new BooleanWrapperFilteredData.Parser(',', SEP);
		fetcher = new DefaultFilteredDataFetcher<>(
				DataType.create("Filtered boolean", BooleanWrapperFilteredData.class) );
	}
	
	@Override
	Stream<Arguments> testFilter() {
		return Stream.of(

				Arguments.of(
						createTestInstance(),
						new Result.ResultBuilder(
								new Coordinate(), Collections.nCopies(2, LibraryType.UNSTRANDED))
						.with(0, 0, h(false), parser, fetcher.getDataType())
						.with(1, 0, h(false), parser, fetcher.getDataType())
						.build().getParellelData(),
						false),

				Arguments.of(
						createTestInstance(),
						new Result.ResultBuilder(
								new Coordinate(), Collections.nCopies(2, LibraryType.UNSTRANDED))
						.with(0, 0, h(true), parser, fetcher.getDataType())
						.with(1, 0, h(false), parser, fetcher.getDataType())
						.build().getParellelData(),
						true),
				
				Arguments.of(
						createTestInstance(),
						new Result.ResultBuilder(
								new Coordinate(), Collections.nCopies(2, LibraryType.UNSTRANDED))
						.with(0, 0, h(false), parser, fetcher.getDataType())
						.with(1, 0, h(true), parser, fetcher.getDataType())
						.build().getParellelData(),
						true),

				Arguments.of(
						createTestInstance(),
						new Result.ResultBuilder(
								new Coordinate(), Collections.nCopies(2, LibraryType.UNSTRANDED))
						.with(0, 0, h(true), parser, fetcher.getDataType())
						.with(1, 0, h(true), parser, fetcher.getDataType())
						.build().getParellelData(),
						true),
				
				Arguments.of(
						createTestInstance(),
						new Result.ResultBuilder(
								new Coordinate(), Collections.nCopies(2, LibraryType.UNSTRANDED))
						.with(0, 0, h(false), parser, fetcher.getDataType())
						.with(0, 1, h(false), parser, fetcher.getDataType())
						.with(1, 0, h(false), parser, fetcher.getDataType())
						.with(1, 1, h(false), parser, fetcher.getDataType())
						.build().getParellelData(),
						false),
				
				Arguments.of(
						createTestInstance(),
						new Result.ResultBuilder(
								new Coordinate(), Collections.nCopies(2, LibraryType.UNSTRANDED))
						.with(0, 0, h(true), parser, fetcher.getDataType())
						.with(0, 1, h(false), parser, fetcher.getDataType())
						.with(1, 0, h(false), parser, fetcher.getDataType())
						.with(1, 1, h(false), parser, fetcher.getDataType())
						.build().getParellelData(),
						true),
				
				Arguments.of(
						createTestInstance(),
						new Result.ResultBuilder(
								new Coordinate(), Collections.nCopies(2, LibraryType.UNSTRANDED))
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
	
	Filter createTestInstance() {
		final int overhang = 0;
		final HomopolymerFilter homopolymerFilter = new HomopolymerFilter(
				c, overhang, new SpecificFilteredDataFetcher<>(c, fetcher) );

		return new AbstractFilterWrapper<HomopolymerFilter>(homopolymerFilter) {
			
			@Override
			public String toString() {
				return "overhang: " + overhang;
			}

		};
	}
	
}
