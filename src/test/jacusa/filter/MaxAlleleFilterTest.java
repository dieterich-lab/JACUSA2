package test.jacusa.filter;

import java.util.Collections;
import java.util.stream.Stream;

import org.junit.jupiter.params.provider.Arguments;

import jacusa.filter.Filter;
import jacusa.filter.MaxAlleleFilter;
import lib.data.DataType;
import lib.data.ParallelData;
import lib.data.count.basecall.BaseCallCount;
import lib.data.count.basecall.DefaultBCC;
import lib.data.fetcher.DataTypeFetcher;
import lib.data.result.Result;
import lib.util.LibraryType;
import lib.util.Parser;
import lib.util.coordinate.OneCoordinate;

/**
 * Tests @see jacusa.filter.MaxAlleleFilter#filter(lib.data.ParallelData)
 */
class MaxAlleleFilterTest extends AbstractFilterTest {

	private final Parser<BaseCallCount> bccParser; 
	private final DataTypeFetcher<BaseCallCount> bccFetcher;
	
	private int testNumber;
	
	public MaxAlleleFilterTest() {
		bccParser 	= new DefaultBCC.Parser(',', '*');
		bccFetcher 	= new DataTypeFetcher<>(DataType.retrieve("Observed", BaseCallCount.class));
		
		testNumber 	= 0;
	}
	
	@Override
	Stream<Arguments> testFilter() {
		return Stream.of(
				
				createArguments(
						1,
						new Result.ResultBuilder(
								new OneCoordinate(), Collections.nCopies(1, LibraryType.UNSTRANDED))
						.with(0, 0, "1,0,0,0", bccParser, bccFetcher.getDataType())
						.with(0, 1, "1,0,0,0", bccParser, bccFetcher.getDataType())
						.with(0, 2, "1,0,0,0", bccParser, bccFetcher.getDataType())
						.with(0, 3, "1,0,0,0", bccParser, bccFetcher.getDataType())
						.build().getParellelData(),
						false),

				createArguments(
						1,
						new Result.ResultBuilder(
								new OneCoordinate(), Collections.nCopies(1, LibraryType.UNSTRANDED))
						.with(0, 0, "1,0,0,0", bccParser, bccFetcher.getDataType())
						.with(0, 1, "0,1,0,0", bccParser, bccFetcher.getDataType())
						.with(0, 2, "0,0,1,0", bccParser, bccFetcher.getDataType())
						.with(0, 3, "0,0,0,1", bccParser, bccFetcher.getDataType())
						.build().getParellelData(),
						true),
				
				createArguments(
						1,
						new Result.ResultBuilder(
								new OneCoordinate(), Collections.nCopies(4, LibraryType.UNSTRANDED))
						.with(0, 0, "1,0,0,0", bccParser, bccFetcher.getDataType())
						.with(1, 0, "0,1,0,0", bccParser, bccFetcher.getDataType())
						.with(2, 0, "0,0,1,0", bccParser, bccFetcher.getDataType())
						.with(3, 0, "0,0,0,1", bccParser, bccFetcher.getDataType())
						.build().getParellelData(),
						true),
				
				createArguments(
						4,
						new Result.ResultBuilder(
								new OneCoordinate(), Collections.nCopies(4, LibraryType.UNSTRANDED))
						.with(0, 0, "1,0,0,0", bccParser, bccFetcher.getDataType())
						.with(1, 0, "0,1,0,0", bccParser, bccFetcher.getDataType())
						.with(2, 0, "0,0,1,0", bccParser, bccFetcher.getDataType())
						.with(3, 0, "0,0,0,1", bccParser, bccFetcher.getDataType())
						.build().getParellelData(),
						false) );
	}

	Arguments createArguments(
			final int maxAlleles, final ParallelData parallelData, final boolean expected) {
		
		return Arguments.of(
				createTestInstance(maxAlleles),
				parallelData,
				expected,
				new StringBuilder()
				.append("maxAlleles: ").append(maxAlleles).append(' ')
				.append("test: ").append(++testNumber)
				.toString() );
	}
	
	Filter createTestInstance(final int maxAlleles) {
		return new MaxAlleleFilter(' ', maxAlleles, bccFetcher);
	}
}
