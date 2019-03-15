package test.lib.data.validator.paralleldata;

import java.util.Arrays;
import java.util.stream.Stream;

import org.junit.jupiter.params.provider.Arguments;

import lib.data.DataType;
import lib.data.ParallelData;
import lib.data.DataContainer.AbstractBuilderFactory;
import lib.data.DataContainer.DefaultBuilderFactory;
import lib.data.count.basecall.BaseCallCount;
import lib.data.fetcher.DataTypeFetcher;
import lib.data.validator.paralleldata.ExtendedVariantSiteValidator;
import lib.util.Base;
import lib.util.LibraryType;
import lib.util.coordinate.OneCoordinate;

class NonHomozygousSiteTest extends AbstractParallelDataValidatorTest {

	private final AbstractBuilderFactory builderFactory;
	private final DataTypeFetcher<BaseCallCount> bccFetcher;
	
	public NonHomozygousSiteTest() {
		builderFactory = new DefaultBuilderFactory();
		bccFetcher = new DataTypeFetcher<>(DataType.retrieve("Test", BaseCallCount.class));
		setTestInstance(
				new ExtendedVariantSiteValidator(bccFetcher));
	}

	@Override
	public Stream<Arguments> testIsValid() {
		return Stream.of(
				
				Arguments.of(
						new ParallelData.Builder(1, Arrays.asList(1))
							.withReplicate(0, 0, builderFactory.createBuilder(new OneCoordinate(), LibraryType.UNSTRANDED)
									.withReferenceBase(Base.N)
									.with(	
											bccFetcher.getDataType(),
											BaseCallCount.create().set(Base.T, 10))
									.build())
							.build(),
						false),
				
				Arguments.of(
						new ParallelData.Builder(2, Arrays.asList(1, 1))
							.withReplicate(0, 0, builderFactory.createBuilder(new OneCoordinate(), LibraryType.UNSTRANDED)
									.withReferenceBase(Base.A)
									.with(	
											bccFetcher.getDataType(),
											BaseCallCount.create().set(Base.T, 10))
									.build())
							.withReplicate(1, 0, builderFactory.createBuilder(new OneCoordinate(), LibraryType.UNSTRANDED)
									.withReferenceBase(Base.N)
									.with(	
											bccFetcher.getDataType(),
											BaseCallCount.create().set(Base.A, 10))
									.build())
							.build(),
						true),
				
				Arguments.of(
						new ParallelData.Builder(3, Arrays.asList(2, 2, 2))
							.withReplicate(0, 0, builderFactory.createBuilder(new OneCoordinate(), LibraryType.UNSTRANDED)
									.withReferenceBase(Base.N)
									.with(	
											bccFetcher.getDataType(),
											BaseCallCount.create().set(Base.A, 10))
									.build())
							.withReplicate(0, 1, builderFactory.createBuilder(new OneCoordinate(), LibraryType.UNSTRANDED)
									.withReferenceBase(Base.N)
									.with(	
											bccFetcher.getDataType(),
											BaseCallCount.create().set(Base.A, 10))

									.build())
							.withReplicate(1, 0, builderFactory.createBuilder(new OneCoordinate(), LibraryType.UNSTRANDED)
									.withReferenceBase(Base.N)
									.with(	
											bccFetcher.getDataType(),
											BaseCallCount.create().set(Base.A, 10))

									.build())
							.withReplicate(1, 1, builderFactory.createBuilder(new OneCoordinate(), LibraryType.UNSTRANDED)
									.withReferenceBase(Base.N)
									.with(	
											bccFetcher.getDataType(),
											BaseCallCount.create().set(Base.A, 10))

									.build())
							.withReplicate(2, 0, builderFactory.createBuilder(new OneCoordinate(), LibraryType.UNSTRANDED)
									.withReferenceBase(Base.N)
									.with(	
											bccFetcher.getDataType(),
											BaseCallCount.create()
												.set(Base.A, 10)
												.set(Base.T, 10))
									.build())
							.withReplicate(2, 1, builderFactory.createBuilder(new OneCoordinate(), LibraryType.UNSTRANDED)
									.withReferenceBase(Base.N)
									.with(	
											bccFetcher.getDataType(),
											BaseCallCount.create()
												.set(Base.A, 10)
												.set(Base.T, 10))
									.build())
							.build(),
						true));
	}

}