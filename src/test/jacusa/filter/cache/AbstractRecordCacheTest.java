package test.jacusa.filter.cache;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;

import htsjdk.samtools.SAMFileHeader;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMFileHeader.SortOrder;
import htsjdk.samtools.util.CloseableIterator;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.cache.container.CacheContainer;
import lib.data.cache.container.ComplexSharedCache;
import lib.data.cache.container.FRPairedEnd2CacheContainer;
import lib.data.cache.container.RFPairedEnd1CacheContainer;
import lib.data.cache.container.ReferenceProvider;
import lib.data.cache.container.SharedCache;
import lib.data.cache.container.SimpleReferenceProvider;
import lib.data.cache.container.UnstrandedCacheContainter;
import lib.data.cache.record.RecordWrapperProcessor;
import lib.data.has.LibraryType;
import lib.location.CoordinateAdvancer;
import lib.location.StrandedCoordinateAdvancer;
import lib.location.UnstrandedCoordinateAdvancer;
import lib.util.coordinate.Coordinate;
import lib.util.coordinate.CoordinateController;
import lib.util.coordinate.CoordinateUtil;
import lib.util.coordinate.CoordinateUtil.STRAND;
import test.utlis.SAMRecordCollectionBuilder;

public abstract class AbstractRecordCacheTest {
	
	private final SortOrder sortOrder; 

	private int activeWindowSize;
	private LibraryType libraryType;
	private String contig;
	private String refSeq;
	
	private SAMFileHeader header; 
	private CoordinateAdvancer coordinateAdvancer;
	private CoordinateController coordinateController;
	private ReferenceProvider referenceProvider;
	private SharedCache sharedCache;
	
	private CacheContainer cacheContainer;
	
	private SAMRecordCollectionBuilder recordBuilder;
	
	public AbstractRecordCacheTest() {
		sortOrder = SortOrder.coordinate;
	}
	
	@BeforeEach
	protected void beforeEach() {
		activeWindowSize = -1;
		libraryType = null;
		contig = new String();
		refSeq = new String();
		
		header = null;
		coordinateAdvancer = null;
		coordinateController = null;
		referenceProvider = null;
		sharedCache = null;

		cacheContainer = null;
		
		recordBuilder = null;
	}
	
	private List<RecordWrapperProcessor> createCaches() {
		return Arrays.asList(createTestInstance());
	}
	
	protected CacheContainer getCacheContainer() {
		return cacheContainer;
	}
	
	protected int getActiveWindowSize() {
		return activeWindowSize;
	}
	
	protected CacheContainer createCacheContainer(
			final LibraryType libraryType,
			final SharedCache sharedCache) {
		switch (libraryType) {
		case UNSTRANDED:
			return new UnstrandedCacheContainter(sharedCache, createCaches());
			
		case RF_FIRSTSTRAND:
			return new RFPairedEnd1CacheContainer(
					new UnstrandedCacheContainter(sharedCache, createCaches()),
					new UnstrandedCacheContainter(sharedCache, createCaches()) );
			
		case FR_SECONDSTRAND:
			return new FRPairedEnd2CacheContainer(
					new UnstrandedCacheContainter(sharedCache, createCaches()),
					new UnstrandedCacheContainter(sharedCache, createCaches()) );

		default:
			throw new IllegalArgumentException("Unsupported library type: " + libraryType.toString());
		}
	}
	
	private CoordinateAdvancer createCoordinateAdvancer(final LibraryType libraryType) {
		switch (libraryType) {
		case UNSTRANDED:
			return new UnstrandedCoordinateAdvancer(new Coordinate());
			
		case RF_FIRSTSTRAND:
			return new StrandedCoordinateAdvancer(new Coordinate());
			
		case FR_SECONDSTRAND:
			return new StrandedCoordinateAdvancer(new Coordinate());
			
		default:
			throw new IllegalArgumentException("Unsupported library type: " + libraryType.toString());
		}
	}

	private boolean isStranded(final LibraryType libraryType) {
		switch (libraryType) {
		case UNSTRANDED:
			return false;
			
		case RF_FIRSTSTRAND:
			return true;
			
		case FR_SECONDSTRAND:
			return true;
			
		default:
			throw new IllegalArgumentException("Unsupported library type: " + libraryType.toString());
		}
	}
	
	protected void update(
			final int activeWindowSize, final LibraryType libraryType, 
			final String contig, final String refSeq) {
		
		coordinateAdvancer = createCoordinateAdvancer(libraryType);

		this.activeWindowSize = activeWindowSize;
		this.libraryType = libraryType;
		this.contig = contig;
		this.refSeq = refSeq;
		
		final Map<String, String> contig2refSeq = new HashMap<>();
		contig2refSeq.put(contig, refSeq);

		header = SAMRecordCollectionBuilder.createHeader(sortOrder, contig2refSeq);
		coordinateController = new CoordinateController(activeWindowSize, coordinateAdvancer);
		referenceProvider = new SimpleReferenceProvider(coordinateController, contig2refSeq);
		sharedCache = new ComplexSharedCache(referenceProvider);
		if (isStranded(libraryType)) {
			coordinateController.updateReserved(new Coordinate(contig, 1, refSeq.length(), STRAND.FORWARD));
		} else {
			coordinateController.updateReserved(new Coordinate(contig, 1, refSeq.length()));
		}
		recordBuilder = new SAMRecordCollectionBuilder(true, SortOrder.coordinate, contig, refSeq, getHeader());
		
		cacheContainer = createCacheContainer(libraryType, sharedCache);
	}
	
	protected void test() {
		int windowIndex = -1;
		
		while (coordinateController.hasNext()) {
			cacheContainer.clear();

			final Coordinate active = coordinateController.next();
			windowIndex++;

			// create location specific iterator
			final CloseableIterator<SAMRecord> it = createIterator(
							active.getContig(), active.getStart(), active.getEnd() );
			
			// add and process records
			while (it.hasNext()) {
				final SAMRecordWrapper recordWrapper = new SAMRecordWrapper(it.next());
				cacheContainer.process(recordWrapper);
			}
			it.close();
			
			// test expected vs. actual
			assertEqualWindow(windowIndex, active);
		}
	}

	protected void assertEqualWindow(int windowIndex, Coordinate active) {
		final Coordinate current = getCoordinateAdvancer().getCurrentCoordinate();
		while (CoordinateUtil.isContained(
				active, 
				current.getPosition())) {

			assertEqual(windowIndex, current);
			getCoordinateAdvancer().advance();
		}
	}
	
	protected String getContig() {
		return contig;
	}
	
	public String getRefSeq() {
		return refSeq;
	}
	
	public SAMFileHeader getHeader() {
		return header;
	}
	
	protected SAMRecordCollectionBuilder getRecordBuilder() {
		return recordBuilder;
	}

	protected SharedCache getShareCache() {
		return sharedCache;
	}

	protected CoordinateAdvancer getCoordinateAdvancer() {
		return coordinateAdvancer;
	}

	protected LibraryType getLibraryType() {
		return libraryType;
	}
	
	protected abstract CloseableIterator<SAMRecord> createIterator(
			final String contig, final int start, final int end);
	protected abstract RecordWrapperProcessor createTestInstance();

	protected abstract void assertEqual(final int windowIndex, final Coordinate current);
	
	protected int getWindowPosition(final int windowIndex, final Coordinate current) {
		int windowPosition = coordinateController.getCoordinateTranslator().convert2windowPosition(current);
		if (isStranded(libraryType)) {
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
