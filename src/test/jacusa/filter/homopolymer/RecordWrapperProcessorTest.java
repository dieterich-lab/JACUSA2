package test.jacusa.filter.homopolymer;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.util.CloseableIterator;
import lib.data.DataTypeContainer;
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
import lib.util.coordinate.DefaultCoordinateTranslator;
import test.utlis.SAMRecordIterator;

public interface RecordWrapperProcessorTest<T> {
	
	@ParameterizedTest(name = "{4}, Lib.={1}")
	@MethodSource("testAddRecordWrapper")
	default void testAddRecordWrapper(
			RecordWrapperProcessor testInstance,
			LibraryType libraryType,
			Collection<SAMRecord> records,
			List<T> expected,
			String info) {
		
		final SharedCache sharedCache = testInstance.getShareCache();
		
		// create new test instance and wrap in container
		CacheContainer testInstanceContainer = new CacheContainer.Builder(libraryType, sharedCache)
				.withCache(testInstance)
				.build();
		
		runTest(
				testInstanceContainer,
				libraryType,
				records, 
				expected);
	}
	
	default void runTest(
			final CacheContainer testInstanceContainer,
			final LibraryType libraryType,
			final Collection<SAMRecord> records,
			final List<T> expected) {
		
		int windowIndex = -1;
		
		final CoordinateController coordinateController = 
				testInstanceContainer.getReferenceProvider().getCoordinateController();
		while (coordinateController.hasNext()) {
			testInstanceContainer.clear();
			
			final Coordinate activeCoordinates = coordinateController.next();
			testInstanceContainer.getReferenceProvider().update();
			windowIndex++;

			// create location specific iterator
			final CloseableIterator<SAMRecord> it = new SAMRecordIterator(
					activeCoordinates,
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
					libraryType,  
					activeCoordinates, expected.get(windowIndex));
		}
	}

	default void assertEqualWindow(
			final CacheContainer testInstanceContainer,
			final LibraryType libraryType,  
			final Coordinate activeWindow, final T expected) {
		
		final CoordinateAdvancer coordinateAdvancer = new CoordinateAdvancer.Builder(libraryType)
				.withCoordinate(activeWindow)
				.build();
		final CoordinateTranslator coordinateTranslator = new DefaultCoordinateTranslator(activeWindow);
		
		// current position within activeWindow
		final Coordinate currentCoordinate = coordinateAdvancer.getCurrentCoordinate();
		while (activeWindow.overlaps(currentCoordinate)) {
			final Base refBase = testInstanceContainer.getReferenceProvider()
					.getReferenceBase(currentCoordinate);
			final DataTypeContainer container = 
					createDataTypeContainer(currentCoordinate, libraryType, refBase);
			testInstanceContainer.populate(container, currentCoordinate);
			
			final int windowPosition = 
					coordinateTranslator.coordinate2windowPosition(currentCoordinate);
			assertEqual(windowPosition, currentCoordinate, container, expected);
			coordinateAdvancer.advance();
		}
	}
	
	default DataTypeContainer.AbstractBuilder createDataTypeContainerBuilder(
			Coordinate coordinate, LibraryType libraryType, Base refBase) {
		
		// create data type container that will store homopolymer info
		return new DataTypeContainer.DefaultBuilderFactory()
				.createBuilder(coordinate, libraryType)
				.withReferenceBase(refBase);
	}
		
	DataTypeContainer createDataTypeContainer(
			Coordinate coordinate, LibraryType libraryType, Base refBase);
	
	void assertEqual(
			int windowPosition, Coordinate currentCoordinate, 
			DataTypeContainer container, T expected);

}
