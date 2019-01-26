package test.jacusa.filter.homopolymer;

import java.util.Map;

import lib.data.cache.container.ComplexSharedCache;
import lib.data.cache.container.ReferenceProvider;
import lib.data.cache.container.SharedCache;
import lib.data.cache.container.SimpleReferenceProvider;
import lib.data.has.LibraryType;
import lib.location.CoordinateAdvancer;
import lib.util.coordinate.CoordinateController;
import lib.util.coordinate.CoordinateUtil.STRAND;
import lib.util.coordinate.OneCoordinate;

public class SharedCacheBuilder implements lib.util.Builder<SharedCache> {
	
	private final int activeWindowSize;
	private final LibraryType libraryType;
	private final String contig;
	private final Map<String, String> contig2refSeq;
	
	public SharedCacheBuilder(
			final int activeWindowSize, 
			final LibraryType libraryType,
			final String contig,
			final Map<String, String> contig2refSeq) {
		
		this.activeWindowSize 	= activeWindowSize;
		this.libraryType 		= libraryType;
		this.contig				= contig;
		this.contig2refSeq		= contig2refSeq;
	}
	
	@Override
	public SharedCache build() {
		final CoordinateAdvancer coordinateAdvancer = 
				new CoordinateAdvancer.Builder(libraryType).build();
		final CoordinateController coordinateController = 
				new CoordinateController(activeWindowSize, coordinateAdvancer);
		final String refSeq = contig2refSeq.get(contig);
		
		if (LibraryType.isStranded(libraryType)) {
			coordinateController.updateReserved(
					new OneCoordinate(contig, 1, refSeq.length() - 1, STRAND.FORWARD));
		} else {
			coordinateController.updateReserved(new OneCoordinate(contig, 1, refSeq.length() - 1));
		}
		
		final ReferenceProvider referenceProvider = 
				new SimpleReferenceProvider(coordinateController, contig2refSeq);

		return new ComplexSharedCache(referenceProvider);
	}
		
	
	
}
