package test.jacusa.filter.homopolymer;

import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.util.CloseableIterator;
import jacusa.filter.homopolymer.HomopolymerReadFilterCache;
import lib.data.DataTypeContainer;
import lib.data.DataTypeContainer.AbstractBuilderFactory;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.cache.container.CacheContainer;
import lib.data.cache.container.SharedCache;
import lib.data.cache.record.RecordWrapperProcessor;
import lib.data.has.LibraryType;
import lib.location.CoordinateAdvancer;
import lib.util.Base;
import lib.util.coordinate.Coordinate;
import lib.util.coordinate.CoordinateController;
import lib.util.coordinate.CoordinateTranslator;
import lib.util.coordinate.CoordinateUtil;
import lib.util.coordinate.DefaultCoordinateTranslator;
import test.utlis.SAMRecordIterator;

public interface RecordWrapperProcessorTest<T> {
	
	@ParameterizedTest(name = "Lib.: {1}, Ref.: {2}, {5}")
	@MethodSource("testAddRecordWrapper")
	default void testAddRecordWrapper(
			RecordWrapperProcessor testInstance,
			LibraryType libraryType,
			Base refBase,
			List<SAMRecord> records,
			List<T> expected,
			String info) {
		
		final SharedCache sharedCache = testInstance.getShareCache();
		
		// create new test instance and wrap in container
		CacheContainer testInstanceContainer = new CacheContainer.Builder(libraryType, sharedCache)
				.withCache(testInstance)
				.build();
		
		runTest(
				testInstanceContainer,
				libraryType, refBase,
				records, 
				expected);
	}
	
	default void runTest(
			final CacheContainer testInstanceContainer,
			final LibraryType libraryType,
			final Base refBase,
			final List<SAMRecord> records,
			final List<T> expected) {
		
		int windowIndex = -1;
		
		final CoordinateController coordinateController = 
				testInstanceContainer.getReferenceProvider().getCoordinateController();
		while (coordinateController.hasNext()) {
			testInstanceContainer.clear();

			final Coordinate activeCoordinates = coordinateController.next();
			windowIndex++;

			// create location specific iterator
			final CloseableIterator<SAMRecord> it = new SAMRecordIterator(
					activeCoordinates.getContig(), 
					activeCoordinates.getStart(), activeCoordinates.getEnd(),
					records);
			
			testInstanceContainer.preProcess();
			while (it.hasNext()) {
				final SAMRecordWrapper recordWrapper = new SAMRecordWrapper(it.next());
				testInstanceContainer.process(recordWrapper);
			}
			testInstanceContainer.postProcess();
			it.close();
			
			assertEqualWindow(
					testInstanceContainer,
					libraryType, refBase, 
					activeCoordinates, expected.get(windowIndex));
		}
	}

	default void assertEqualWindow(
			final CacheContainer testInstanceContainer,
			final LibraryType libraryType, final Base refBase, 
			final Coordinate activeWindow, final T expected) {
		
		final CoordinateAdvancer coordinateAdvancer = new CoordinateAdvancer.Builder(libraryType)
				.withCoordinate(activeWindow)
				.build();
		final CoordinateTranslator coordinateTranslator = new DefaultCoordinateTranslator(activeWindow);
		
		// current position within activeWindow
		final Coordinate currentCoordinate = coordinateAdvancer.getCurrentCoordinate();
		while (CoordinateUtil.isContained(
				activeWindow, 
				currentCoordinate.getPosition())) {

			final int windowPosition = coordinateTranslator.convert2windowPosition(currentCoordinate);
			final DataTypeContainer container = 
					createDataTypeContainer(currentCoordinate, libraryType, refBase);
			testInstanceContainer.populate(container, currentCoordinate);
			
			assertEqual(windowPosition, currentCoordinate, container, expected);
			coordinateAdvancer.advance();
		}
	}
	
	default AbstractBuilderFactory getBuilderFactory() {
		return new DataTypeContainer.DefaultBuilderFactory();
	}
	
	DataTypeContainer createDataTypeContainer(
			Coordinate coordinate, LibraryType libraryType, Base refBase);
	
	void assertEqual(
			int windowPosition, Coordinate currentCoordinate, 
			DataTypeContainer container, T expected);

}
