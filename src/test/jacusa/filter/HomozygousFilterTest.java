package test.jacusa.filter;

import java.util.Collections;
import java.util.stream.Stream;

import org.junit.jupiter.params.provider.Arguments;

import jacusa.filter.Filter;
import jacusa.filter.HomozygousFilter;
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
 * Tests @see jacusa.filter.HomozygousFilter#filter(lib.data.ParallelData)
 */
class HomozygousFilterTest extends AbstractFilterTest {

	private final Parser<BaseCallCount> bccParser; 
	private final DataTypeFetcher<BaseCallCount> bccFetcher;
	
	private int testNumber;
	
	public HomozygousFilterTest() {
		bccParser 	= new DefaultBCC.Parser(',', '*');
		bccFetcher 	= new DataTypeFetcher<>(DataType.retrieve("Observed", BaseCallCount.class));
		
		testNumber	= 0;
	}

	@Override
	Stream<Arguments> testFilter() {
		return Stream.of(
				
				createArguments(
						0, 
						new Result.ResultBuilder(
								new OneCoordinate(), Collections.nCopies(2, LibraryType.UNSTRANDED))
						.with(0, 0, "1,0,0,0", bccParser, bccFetcher.getDataType())
						.with(1, 0, "1,0,0,0", bccParser, bccFetcher.getDataType())
						.build().getParellelData(),
						false),
				
				createArguments(
						1,
						new Result.ResultBuilder(
								new OneCoordinate(), Collections.nCopies(2, LibraryType.UNSTRANDED))
						.with(0, 0, "1,0,0,0", bccParser, bccFetcher.getDataType())
						.with(1, 0, "1,0,0,0", bccParser, bccFetcher.getDataType())
						.build().getParellelData(),
						false),

				createArguments(
						0,
						new Result.ResultBuilder(
								new OneCoordinate(), Collections.nCopies(2, LibraryType.UNSTRANDED))
						.with(0, 0, "1,0,0,0", bccParser, bccFetcher.getDataType())
						.with(1, 0, "1,1,0,0", bccParser, bccFetcher.getDataType())
						.build().getParellelData(),
						false),
				
				createArguments(
						1,
						new Result.ResultBuilder(
								new OneCoordinate(), Collections.nCopies(2, LibraryType.UNSTRANDED))
						.with(0, 0, "1,1,0,0", bccParser, bccFetcher.getDataType())
						.with(1, 0, "1,0,0,0", bccParser, bccFetcher.getDataType())
						.build().getParellelData(),
						false),

				createArguments(
						0,
						new Result.ResultBuilder(
								new OneCoordinate(), Collections.nCopies(2, LibraryType.UNSTRANDED))
						.with(0, 0, "1,1,0,0", bccParser, bccFetcher.getDataType())
						.with(1, 0, "1,0,0,0", bccParser, bccFetcher.getDataType())
						.build().getParellelData(),
						true),
				
				createArguments(
						1,
						new Result.ResultBuilder(
								new OneCoordinate(), Collections.nCopies(2, LibraryType.UNSTRANDED))
						.with(0, 0, "1,0,0,0", bccParser, bccFetcher.getDataType())
						.with(1, 0, "1,1,0,0", bccParser, bccFetcher.getDataType())
						.build().getParellelData(),
						true),
				
				createArguments(
						0,
						new Result.ResultBuilder(
								new OneCoordinate(), Collections.nCopies(2, LibraryType.UNSTRANDED))
						.with(0, 0, "1,0,0,0", bccParser, bccFetcher.getDataType())
						.with(0, 1, "1,0,0,0", bccParser, bccFetcher.getDataType())
						.with(1, 0, "1,0,0,0", bccParser, bccFetcher.getDataType())
						.with(1, 1, "1,0,0,0", bccParser, bccFetcher.getDataType())
						.build().getParellelData(),
						false),
				
				createArguments(
						1,
						new Result.ResultBuilder(
								new OneCoordinate(), Collections.nCopies(2, LibraryType.UNSTRANDED))
						.with(0, 0, "1,0,0,0", bccParser, bccFetcher.getDataType())
						.with(0, 1, "1,0,0,0", bccParser, bccFetcher.getDataType())
						.with(1, 0, "1,0,0,0", bccParser, bccFetcher.getDataType())
						.with(1, 1, "1,0,0,0", bccParser, bccFetcher.getDataType())
						.build().getParellelData(),
						false),
				
				createArguments(
						0,
						new Result.ResultBuilder(
								new OneCoordinate(), Collections.nCopies(2, LibraryType.UNSTRANDED))
						.with(0, 0, "1,0,0,0", bccParser, bccFetcher.getDataType())
						.with(0, 1, "1,1,0,0", bccParser, bccFetcher.getDataType())
						.with(1, 0, "1,0,0,0", bccParser, bccFetcher.getDataType())
						.with(1, 1, "1,0,0,0", bccParser, bccFetcher.getDataType())
						.build().getParellelData(),
						true),
				
				createArguments(
						1,
						new Result.ResultBuilder(
								new OneCoordinate(), Collections.nCopies(2, LibraryType.UNSTRANDED))
						.with(0, 0, "1,0,0,0", bccParser, bccFetcher.getDataType())
						.with(0, 1, "1,0,0,0", bccParser, bccFetcher.getDataType())
						.with(1, 0, "1,1,0,0", bccParser, bccFetcher.getDataType())
						.with(1, 1, "1,0,0,0", bccParser, bccFetcher.getDataType())
						.build().getParellelData(),
						true) );
	}
	
	Arguments createArguments(
			final int conditionIndex, final ParallelData parallelData, final boolean expected) {
		
		return Arguments.of(
				createTestInstance(conditionIndex),
				parallelData,
				expected,
				new StringBuilder()
				.append("conditionIndex: ").append(conditionIndex).append(' ')
				.append("test: ").append(++testNumber)
				.toString() );
	}
	
	Filter createTestInstance(final int conditionIndex) {
		return new HomozygousFilter(' ', conditionIndex, bccFetcher);
	}

}
