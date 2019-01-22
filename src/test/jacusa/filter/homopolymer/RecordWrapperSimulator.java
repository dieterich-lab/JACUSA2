package test.jacusa.filter.homopolymer;

import htsjdk.samtools.SAMFileHeader.SortOrder;
import lib.data.cache.container.ComplexSharedCache;
import lib.data.cache.container.ReferenceProvider;
import lib.data.cache.container.SharedCache;
import lib.data.cache.container.SimpleReferenceProvider;
import lib.data.has.LibraryType;
import lib.location.CoordinateAdvancer;
import lib.util.coordinate.Coordinate;
import lib.util.coordinate.CoordinateController;
import lib.util.coordinate.CoordinateUtil.STRAND;
import test.utlis.SAMRecordCollectionBuilder;

// JUNIT: A
public class RecordWrapperSimulator {
	
	private int activeWindowSize;
	private LibraryType libraryType;
	
	private SAMRecordCollectionBuilder recordBuilder;
	
	private CoordinateAdvancer coordinateAdvancer;
	private CoordinateController coordinateController;
	private ReferenceProvider referenceProvider;
	private SharedCache sharedCache;
	
	public void update(
			final int activeWindowSize, 
			final LibraryType libraryType, 
			final String contig, final String refSeq) {
		
		this.activeWindowSize 	= activeWindowSize;
		this.libraryType 		= libraryType;

		recordBuilder = new SAMRecordCollectionBuilder.Builder(SortOrder.coordinate, contig)
				.withReferenceSequence(contig, refSeq)
				.build();
		
		coordinateAdvancer 		= new CoordinateAdvancer.Builder(libraryType).build();
		coordinateController 	= new CoordinateController(activeWindowSize, coordinateAdvancer);
		referenceProvider 		= new SimpleReferenceProvider(coordinateController, recordBuilder.getContig2ReferenceSequence());
		sharedCache 			= new ComplexSharedCache(referenceProvider);
		if (LibraryType.isStranded(libraryType)) {
			coordinateController.updateReserved(new Coordinate(contig, 1, refSeq.length(), STRAND.FORWARD));
		} else {
			coordinateController.updateReserved(new Coordinate(contig, 1, refSeq.length()));
		}
	}
	
	public SAMRecordCollectionBuilder getRecordBuilder() {
		return recordBuilder;
	}

	public SharedCache getShareCache() {
		return sharedCache;
	}

	public CoordinateController getCoordinateController() {
		return coordinateController;
	}
	
	public CoordinateAdvancer getCoordinateAdvancer() {
		return coordinateAdvancer;
	}

	public LibraryType getLibraryType() {
		return libraryType;
	}

	public int getActiveWindowSize() {
		return activeWindowSize;
	}
	
	public int getWindowPosition(final int windowIndex, final Coordinate current) {
		int windowPosition = coordinateController.getCoordinateTranslator().convert2windowPosition(current);
		if (LibraryType.isStranded(libraryType)) {
			windowPosition = 2 * windowPosition + windowIndex * 2 * activeWindowSize;
			if (current.getStrand() == STRAND.REVERSE) {
				windowPosition++;
			}
		} else {
			windowPosition += windowIndex * activeWindowSize;
		}

		return windowPosition;
	}
	
}
