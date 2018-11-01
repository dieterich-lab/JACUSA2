package test.lib.data;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.stream.Stream;

import org.junit.jupiter.params.provider.Arguments;

import jacusa.JACUSA;
import jacusa.method.pileup.PileupMethod;
import lib.data.DataType;
import lib.data.DataTypeContainer;
import lib.data.count.PileupCount;
import lib.data.count.basecall.ArrayBaseCallCount;
import lib.data.filter.BaseCallCountFilteredData;
import lib.data.filter.BooleanWrapperFilteredData;
import lib.data.filter.BooleanWrapper;
import lib.data.has.LibraryType;
import lib.util.Base;
import lib.util.coordinate.Coordinate;
import lib.util.coordinate.CoordinateUtil.STRAND;
import test.utlis.MyAssert;

class PileupDataTest extends AbstractDataTest {

	public PileupDataTest() {
		super(
				new PileupMethod.PileupBuilderFactory(null),
				null);
		/*
		 * new PileupMethod.Parser()
				new PileupData.Parser(
				new PileupCount.Parser(new ArrayBaseCallCount.Parser(), new MapBaseCallQualityCount.Parser()), 
				new BaseCallCountFilteredData.Parser(new ArrayBaseCallCount.Parser()), 
				new BooleanFilteredData.Parser()) );
				*/
	}

	@Override
	void testParserParseFail() {
		fail("TODO implement");
	}
	
	@Override
	void testCopySpecific(DataTypeContainer data, DataTypeContainer copy) {
		MyAssert.assertCopy(
				data.getPileupCount(),
				copy.getPileupCount(),
				"PileupCount");
		MyAssert.assertCopy(
				data.getBaseCallCountFilteredData(), 
				copy.getBaseCallCountFilteredData(), 
				"BaseCallCountFilterData");
		MyAssert.assertCopy(
				data.getBooleanFilteredData(), 
				copy.getBooleanFilteredData(), 
				"BooleanFilterData");
	}

	@Override
	Stream<Arguments> testParserParse() {
		return Stream.of(
				Arguments.of(
						"A	UNSTRANDED	chr1:1-2:+	* 10;10;10;10,20;20;20,30;30,40	*	*", 
						getBuilderFactory()
							.createBuilder(
									new Coordinate("chr1", 1, 2, STRAND.FORWARD), LibraryType.UNSTRANDED)
							.withReferenceBase(Base.A)
							.with(
									DataType.PILEUP_COUNT, 
									new PileupCount(JACUSA.bcqcFactory.create()
										.set(Base.A, (byte)10, 4)
										.set(Base.C, (byte)20, 3)
										.set(Base.G, (byte)30, 2)
										.set(Base.T, (byte)40, 1)))
							.build()) );
	}

	@Override
	Stream<Arguments> testParserWrap() {
		return Stream.of(
				Arguments.of(
						getBuilderFactory()
						.createBuilder(
								new Coordinate("chr1", 1, 2, STRAND.FORWARD), LibraryType.UNSTRANDED)							
							.withReferenceBase(Base.A)
							.with(
									DataType.PILEUP_COUNT, 
									new PileupCount(JACUSA.bcqcFactory.create()
										.set(Base.A, (byte)10, 4)
										.set(Base.C, (byte)20, 3)
										.set(Base.G, (byte)30, 2)
										.set(Base.T, (byte)40, 1)))
							.build(),
						"A	UNSTRANDED	chr1:1-2:+	* 10;10;10;10,20;20;20,30;30,40	*	*") );
	}

	@Override
	Stream<Arguments> testCopy() {
		return Stream.of(
				Arguments.of(
						getBuilderFactory()
							.createBuilder(
								new Coordinate("chr1", 1, 2, STRAND.FORWARD), LibraryType.UNSTRANDED)
							.withReferenceBase(Base.A)
							.with(
									DataType.PILEUP_COUNT, 
									new PileupCount(JACUSA.bcqcFactory.create()
										.set(Base.A, (byte)10, 4)
										.set(Base.C, (byte)20, 3)
										.set(Base.G, (byte)30, 2)
										.set(Base.T, (byte)40, 1)))
							.with(
									DataType.F_BCC, 
									new BaseCallCountFilteredData()
										.add('A', new ArrayBaseCallCount()
												.set(Base.C, 5))
										.add('B', new ArrayBaseCallCount()
										.set(Base.C, 5)))
							.with(
									DataType.F_BOOLEAN,
									new BooleanWrapperFilteredData()
										.add('C', new BooleanWrapper(true))
										.add('D', new BooleanWrapper(false)))
							.build()) );
	}

	@Override
	Stream<Arguments> testMerge() {
		return Stream.of(
				Arguments.of(
						
						getBuilderFactory()
							.createBuilder(
								new Coordinate("chr1", 1, 2, STRAND.FORWARD), LibraryType.UNSTRANDED)
							.withReferenceBase(Base.N)
							.with(
									DataType.PILEUP_COUNT, 
									new PileupCount(JACUSA.bcqcFactory.create()
										.set(Base.A, (byte)10, 4)
										.set(Base.C, (byte)20, 3)
										.set(Base.G, (byte)30, 2)
										.set(Base.T, (byte)40, 1)))
							.with(
									DataType.F_BCC,
									new BaseCallCountFilteredData()
										.add('A', new ArrayBaseCallCount()
												.set(Base.C, 5))
										.add('B', new ArrayBaseCallCount()
											.set(Base.C, 5)))
							.with(
									DataType.F_BOOLEAN,
									new BooleanWrapperFilteredData()
										.add('C', new BooleanWrapper(true))
										.add('D', new BooleanWrapper(false)))
							.build(),
						
						getBuilderFactory()
							.createBuilder(
									new Coordinate("chr1", 1, 2, STRAND.FORWARD), LibraryType.FR_SECONDSTRAND)
							.withReferenceBase(Base.A)
							.with(
									DataType.PILEUP_COUNT, 
									new PileupCount(JACUSA.bcqcFactory.create()
										.set(Base.A, (byte)10, 1)
										.set(Base.C, (byte)20, 2)
										.set(Base.G, (byte)30, 3)
										.set(Base.T, (byte)40, 4)))
							.with(
									DataType.F_BCC,
									new BaseCallCountFilteredData()
										.add('A', new ArrayBaseCallCount()
											.set(Base.C, 5)))
							.with(
									DataType.F_BOOLEAN,
									new BooleanWrapperFilteredData()
										.add('C', new BooleanWrapper(false)))
							.build(),
							
						getBuilderFactory()
							.createBuilder(
									new Coordinate("chr1", 1, 2, STRAND.FORWARD), LibraryType.MIXED)
							.withReferenceBase(Base.A)
							.with(
									DataType.PILEUP_COUNT, 
									new PileupCount(JACUSA.bcqcFactory.create()
										.set(Base.A, (byte)10, 5)
										.set(Base.C, (byte)20, 5)
										.set(Base.G, (byte)30, 5)
										.set(Base.T, (byte)40, 5)))
							.with(
									DataType.F_BCC,
									new BaseCallCountFilteredData()
										.add('A', new ArrayBaseCallCount()
												.set(Base.C, 10))
										.add('B', new ArrayBaseCallCount()
												.set(Base.C, 5)))
							.with(
									DataType.F_BOOLEAN,
									new BooleanWrapperFilteredData()
										.add('C', new BooleanWrapper(true))
										.add('D', new BooleanWrapper(false)))
							.build()) );
	}
	
}
