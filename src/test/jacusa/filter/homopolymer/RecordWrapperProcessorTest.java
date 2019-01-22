package test.jacusa.filter.homopolymer;

import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.util.CloseableIterator;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.cache.container.CacheContainer;
import lib.data.cache.record.RecordWrapperProcessor;
import lib.location.CoordinateAdvancer;
import lib.util.coordinate.Coordinate;
import lib.util.coordinate.CoordinateController;
import lib.util.coordinate.CoordinateUtil;
import test.utlis.SAMRecordIterator;

public interface RecordWrapperProcessorTest {
	
	default void runTest() {
		int windowIndex = -1;
		
		final CoordinateController coordinateController = 
				getRecordWrapperSimulator().getCoordinateController();
		final CacheContainer testInstanceContainer = getTestInstanceContainer(); 
		
		while (coordinateController.hasNext()) {
			testInstanceContainer.clear();

			final Coordinate activeWindow = coordinateController.next();
			windowIndex++;

			// create location specific iterator
			final CloseableIterator<SAMRecord> it = createIterator(
							activeWindow.getContig(), 
							activeWindow.getStart(), activeWindow.getEnd() );
			
			testInstanceContainer.preProcess();
			// add and process records
			while (it.hasNext()) {
				final SAMRecordWrapper recordWrapper = new SAMRecordWrapper(it.next());
				testInstanceContainer.process(recordWrapper);
			}
			testInstanceContainer.postProcess();
			it.close();
			
			// test expected vs. actual referenced by index of window and genomic coordinates
			assertEqualWindow(windowIndex, activeWindow);
		}
	}

	default void assertEqualWindow(int windowIndex, Coordinate activeWindow) {
		final CoordinateAdvancer coordinateAdvancer = 
				getRecordWrapperSimulator().getCoordinateAdvancer();
		
		// current position within activeWindow
		final Coordinate current = coordinateAdvancer.getCurrentCoordinate();
		while (CoordinateUtil.isContained(
				activeWindow, 
				current.getPosition())) {

			assertEqual(windowIndex, current);
			coordinateAdvancer.advance();
		}
	}

	RecordWrapperSimulator getRecordWrapperSimulator();
	
	default CloseableIterator<SAMRecord> createIterator(String contig, int start, int end) {
		return new SAMRecordIterator(
				contig, start, end, 
				getRecordWrapperSimulator().getRecordBuilder().getRecords() );
	}
	
	RecordWrapperProcessor createTestInstance();
	CacheContainer getTestInstanceContainer();

	void assertEqual(final int windowIndex, final Coordinate current);
	
}
