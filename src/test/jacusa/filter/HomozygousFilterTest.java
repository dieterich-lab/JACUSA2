package test.jacusa.filter;

import java.util.Collections;
import java.util.stream.Stream;

import org.junit.jupiter.params.provider.Arguments;

import jacusa.filter.Filter;
import jacusa.filter.HomozygousFilter;
import lib.data.DataType;
import lib.data.cache.fetcher.DataTypeFetcher;
import lib.data.count.basecall.BaseCallCount;
import lib.data.count.basecall.DefaultBaseCallCount;
import lib.data.has.LibraryType;
import lib.data.result.Result;
import lib.util.Parser;
import lib.util.coordinate.OneCoordinate;

/**
 * Tests @see jacusa.filter.HomozygousFilter#filter(lib.data.ParallelData)
 */
class HomozygousFilterTest extends AbstractFilterTest {

	private final Parser<BaseCallCount> bccParser; 
	private final DataTypeFetcher<BaseCallCount> bccFetcher;
	
	public HomozygousFilterTest() {
		bccParser 	= new DefaultBaseCallCount.Parser(',', '*');
		bccFetcher 	= new DataTypeFetcher<>(DataType.create("Observed", BaseCallCount.class));
	}

	@Override
	Stream<Arguments> testFilter() {
		return Stream.of(

				Arguments.of(
						createTestInstance(0),
						new Result.ResultBuilder(
								new OneCoordinate(), Collections.nCopies(2, LibraryType.UNSTRANDED))
						.with(0, 0, "1,0,0,0", bccParser, bccFetcher.getDataType())
						.with(1, 0, "1,0,0,0", bccParser, bccFetcher.getDataType())
						.build().getParellelData(),
						false),
				Arguments.of(
						createTestInstance(1),
						new Result.ResultBuilder(
								new OneCoordinate(), Collections.nCopies(2, LibraryType.UNSTRANDED))
						.with(0, 0, "1,0,0,0", bccParser, bccFetcher.getDataType())
						.with(1, 0, "1,0,0,0", bccParser, bccFetcher.getDataType())
						.build().getParellelData(),
						false),

				Arguments.of(
						createTestInstance(0),
						new Result.ResultBuilder(
								new OneCoordinate(), Collections.nCopies(2, LibraryType.UNSTRANDED))
						.with(0, 0, "1,0,0,0", bccParser, bccFetcher.getDataType())
						.with(1, 0, "1,1,0,0", bccParser, bccFetcher.getDataType())
						.build().getParellelData(),
						false),
				Arguments.of(
						createTestInstance(1),
						new Result.ResultBuilder(
								new OneCoordinate(), Collections.nCopies(2, LibraryType.UNSTRANDED))
						.with(0, 0, "1,1,0,0", bccParser, bccFetcher.getDataType())
						.with(1, 0, "1,0,0,0", bccParser, bccFetcher.getDataType())
						.build().getParellelData(),
						false),

				Arguments.of(
						createTestInstance(0),
						new Result.ResultBuilder(
								new OneCoordinate(), Collections.nCopies(2, LibraryType.UNSTRANDED))
						.with(0, 0, "1,1,0,0", bccParser, bccFetcher.getDataType())
						.with(1, 0, "1,0,0,0", bccParser, bccFetcher.getDataType())
						.build().getParellelData(),
						true),
				Arguments.of(
						createTestInstance(1),
						new Result.ResultBuilder(
								new OneCoordinate(), Collections.nCopies(2, LibraryType.UNSTRANDED))
						.with(0, 0, "1,0,0,0", bccParser, bccFetcher.getDataType())
						.with(1, 0, "1,1,0,0", bccParser, bccFetcher.getDataType())
						.build().getParellelData(),
						true),
				
				Arguments.of(
						createTestInstance(0),
						new Result.ResultBuilder(
								new OneCoordinate(), Collections.nCopies(2, LibraryType.UNSTRANDED))
						.with(0, 0, "1,0,0,0", bccParser, bccFetcher.getDataType())
						.with(0, 1, "1,0,0,0", bccParser, bccFetcher.getDataType())
						.with(1, 0, "1,0,0,0", bccParser, bccFetcher.getDataType())
						.with(1, 1, "1,0,0,0", bccParser, bccFetcher.getDataType())
						.build().getParellelData(),
						false),
				Arguments.of(
						createTestInstance(1),
						new Result.ResultBuilder(
								new OneCoordinate(), Collections.nCopies(2, LibraryType.UNSTRANDED))
						.with(0, 0, "1,0,0,0", bccParser, bccFetcher.getDataType())
						.with(0, 1, "1,0,0,0", bccParser, bccFetcher.getDataType())
						.with(1, 0, "1,0,0,0", bccParser, bccFetcher.getDataType())
						.with(1, 1, "1,0,0,0", bccParser, bccFetcher.getDataType())
						.build().getParellelData(),
						false),
				
				Arguments.of(
						createTestInstance(0),
						new Result.ResultBuilder(
								new OneCoordinate(), Collections.nCopies(2, LibraryType.UNSTRANDED))
						.with(0, 0, "1,0,0,0", bccParser, bccFetcher.getDataType())
						.with(0, 1, "1,1,0,0", bccParser, bccFetcher.getDataType())
						.with(1, 0, "1,0,0,0", bccParser, bccFetcher.getDataType())
						.with(1, 1, "1,0,0,0", bccParser, bccFetcher.getDataType())
						.build().getParellelData(),
						true),
				Arguments.of(
						createTestInstance(1),
						new Result.ResultBuilder(
								new OneCoordinate(), Collections.nCopies(2, LibraryType.UNSTRANDED))
						.with(0, 0, "1,0,0,0", bccParser, bccFetcher.getDataType())
						.with(0, 1, "1,0,0,0", bccParser, bccFetcher.getDataType())
						.with(1, 0, "1,1,0,0", bccParser, bccFetcher.getDataType())
						.with(1, 1, "1,0,0,0", bccParser, bccFetcher.getDataType())
						.build().getParellelData(),
						true) );
	}
	
	Filter createTestInstance(final int conditionIndex) {
		final HomozygousFilter homozygousFilter = new HomozygousFilter(' ', conditionIndex, bccFetcher);
		
		return new AbstractFilterWrapper<HomozygousFilter>(homozygousFilter) {
			
			@Override
			public String toString() {
				return "conditionIndex: " + conditionIndex;
			}

		};
	}

}
