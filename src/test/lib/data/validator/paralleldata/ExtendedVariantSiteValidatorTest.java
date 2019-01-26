package test.lib.data.validator.paralleldata;

import java.util.Arrays;
import java.util.stream.Stream;

import org.junit.jupiter.params.provider.Arguments;

import jacusa.JACUSA;
import lib.data.DataTypeContainer.AbstractBuilderFactory;
import lib.data.DataTypeContainer.DefaultBuilderFactory;
import lib.data.DataType;
import lib.data.ParallelData;
import lib.data.cache.fetcher.DataTypeFetcher;
import lib.data.count.basecall.BaseCallCount;
import lib.data.has.LibraryType;
import lib.data.validator.paralleldata.ExtendedVariantSiteValidator;
import lib.util.Base;
import lib.util.coordinate.OneCoordinate;

class ExtendedVariantSiteValidatorTest extends AbstractParallelDataValidatorTest {

	private final AbstractBuilderFactory builderFactory;
	private final DataTypeFetcher<BaseCallCount> bccFetcher;
	
	public ExtendedVariantSiteValidatorTest() {
		builderFactory = new DefaultBuilderFactory();
		bccFetcher = new DataTypeFetcher<>(DataType.create("Test", BaseCallCount.class));
		setTestInstance(
				new ExtendedVariantSiteValidator(bccFetcher));
	}

	/*
	 * Method Source
	 */
	
	@Override
	public Stream<Arguments> testIsValid() {
		return Stream.of(
				
				Arguments.of(
						new ParallelData.Builder(1, Arrays.asList(1))
							.withReplicate(0, 0, builderFactory.createBuilder(new OneCoordinate(), LibraryType.UNSTRANDED)
									.withReferenceBase(Base.N)
									.with(	
											bccFetcher.getDataType(),
											JACUSA.BCC_FACTORY.create().set(Base.T, 10))
									.build())
							.build(),
						false),
				
				Arguments.of(
						new ParallelData.Builder(2, Arrays.asList(1, 1))
							.withReplicate(0, 0, builderFactory.createBuilder(new OneCoordinate(), LibraryType.UNSTRANDED)
									.withReferenceBase(Base.A)
									.with(	
											bccFetcher.getDataType(),
											JACUSA.BCC_FACTORY.create().set(Base.T, 10))
									.build())
							.withReplicate(1, 0, builderFactory.createBuilder(new OneCoordinate(), LibraryType.UNSTRANDED)
									.withReferenceBase(Base.N)
									.with(	
											bccFetcher.getDataType(),
											JACUSA.BCC_FACTORY.create().set(Base.A, 10))
									.build())
							.build(),
						true),
				
				Arguments.of(
						new ParallelData.Builder(3, Arrays.asList(2, 2, 2))
							.withReplicate(0, 0, builderFactory.createBuilder(new OneCoordinate(), LibraryType.UNSTRANDED)
									.withReferenceBase(Base.N)
									.with(	
											bccFetcher.getDataType(),
											JACUSA.BCC_FACTORY.create().set(Base.A, 10))
									.build())
							.withReplicate(0, 1, builderFactory.createBuilder(new OneCoordinate(), LibraryType.UNSTRANDED)
									.withReferenceBase(Base.N)
									.with(	
											bccFetcher.getDataType(),
											JACUSA.BCC_FACTORY.create().set(Base.A, 10))

									.build())
							.withReplicate(1, 0, builderFactory.createBuilder(new OneCoordinate(), LibraryType.UNSTRANDED)
									.withReferenceBase(Base.N)
									.with(	
											bccFetcher.getDataType(),
											JACUSA.BCC_FACTORY.create().set(Base.A, 10))

									.build())
							.withReplicate(1, 1, builderFactory.createBuilder(new OneCoordinate(), LibraryType.UNSTRANDED)
									.withReferenceBase(Base.N)
									.with(	
											bccFetcher.getDataType(),
											JACUSA.BCC_FACTORY.create().set(Base.A, 10))

									.build())
							.withReplicate(2, 0, builderFactory.createBuilder(new OneCoordinate(), LibraryType.UNSTRANDED)
									.withReferenceBase(Base.N)
									.with(	
											bccFetcher.getDataType(),
											JACUSA.BCC_FACTORY.create()
												.set(Base.A, 10)
												.set(Base.T, 10))
									.build())
							.withReplicate(2, 1, builderFactory.createBuilder(new OneCoordinate(), LibraryType.UNSTRANDED)
									.withReferenceBase(Base.N)
									.with(	
											bccFetcher.getDataType(),
											JACUSA.BCC_FACTORY.create()
												.set(Base.A, 10)
												.set(Base.T, 10))
									.build())
							.build(),
						true));
	}
	
}
