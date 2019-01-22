package test.jacusa.filter;


import java.util.Collections;
import java.util.stream.Stream;

import org.junit.jupiter.params.provider.Arguments;

import jacusa.filter.Filter;
import jacusa.filter.MaxAlleleFilter;
import lib.data.DataType;
import lib.data.cache.fetcher.DataTypeFetcher;
import lib.data.count.basecall.BaseCallCount;
import lib.data.count.basecall.DefaultBaseCallCount;
import lib.data.has.LibraryType;
import lib.data.result.Result;
import lib.util.Parser;
import lib.util.coordinate.Coordinate;

/**
 * Tests @see jacusa.filter.MaxAlleleFilter#filter(lib.data.ParallelData)
 */
class MaxAlleleFilterTest extends AbstractFilterTest {

	private final Parser<BaseCallCount> bccParser; 
	private final DataTypeFetcher<BaseCallCount> bccFetcher;
	
	public MaxAlleleFilterTest() {
		bccParser 	= new DefaultBaseCallCount.Parser(',', '*');
		bccFetcher 	= new DataTypeFetcher<>(DataType.create("Observed", BaseCallCount.class));
	}
	
	@Override
	Stream<Arguments> testFilter() {
		return Stream.of(
				
				Arguments.of(
						createTestInstance(1),
						new Result.ResultBuilder(
								new Coordinate(), Collections.nCopies(1, LibraryType.UNSTRANDED))
						.with(0, 0, "1,0,0,0", bccParser, bccFetcher.getDataType())
						.with(0, 1, "1,0,0,0", bccParser, bccFetcher.getDataType())
						.with(0, 2, "1,0,0,0", bccParser, bccFetcher.getDataType())
						.with(0, 3, "1,0,0,0", bccParser, bccFetcher.getDataType())
						.build().getParellelData(),
						false),

				Arguments.of(
						createTestInstance(1),
						new Result.ResultBuilder(
								new Coordinate(), Collections.nCopies(1, LibraryType.UNSTRANDED))
						.with(0, 0, "1,0,0,0", bccParser, bccFetcher.getDataType())
						.with(0, 1, "0,1,0,0", bccParser, bccFetcher.getDataType())
						.with(0, 2, "0,0,1,0", bccParser, bccFetcher.getDataType())
						.with(0, 3, "0,0,0,1", bccParser, bccFetcher.getDataType())
						.build().getParellelData(),
						true),
				
				Arguments.of(
						createTestInstance(1),
						new Result.ResultBuilder(
								new Coordinate(), Collections.nCopies(4, LibraryType.UNSTRANDED))
						.with(0, 0, "1,0,0,0", bccParser, bccFetcher.getDataType())
						.with(1, 0, "0,1,0,0", bccParser, bccFetcher.getDataType())
						.with(2, 0, "0,0,1,0", bccParser, bccFetcher.getDataType())
						.with(3, 0, "0,0,0,1", bccParser, bccFetcher.getDataType())
						.build().getParellelData(),
						true),
				
				Arguments.of(
						createTestInstance(4),
						new Result.ResultBuilder(
								new Coordinate(), Collections.nCopies(4, LibraryType.UNSTRANDED))
						.with(0, 0, "1,0,0,0", bccParser, bccFetcher.getDataType())
						.with(1, 0, "0,1,0,0", bccParser, bccFetcher.getDataType())
						.with(2, 0, "0,0,1,0", bccParser, bccFetcher.getDataType())
						.with(3, 0, "0,0,0,1", bccParser, bccFetcher.getDataType())
						.build().getParellelData(),
						false) );
	}

	Filter createTestInstance(final int maxAlleles) {
		final MaxAlleleFilter maxAlleleFilter = new MaxAlleleFilter(' ', maxAlleles, bccFetcher);
		
		return new AbstractFilterWrapper<MaxAlleleFilter>(maxAlleleFilter) {
			
			@Override
			public String toString() {
				return "maxAllelles: " + maxAlleles;
			}

		};
	}
	
}
