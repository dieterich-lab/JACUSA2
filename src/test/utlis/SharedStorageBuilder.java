package test.utlis;

import java.util.Map;

import lib.data.storage.container.ComplexSharedStorage;
import lib.data.storage.container.ReferenceProvider;
import lib.data.storage.container.SharedStorage;
import lib.data.storage.container.SimpleReferenceProvider;
import lib.util.LibraryType;
import lib.util.coordinate.Coordinate;
import lib.util.coordinate.CoordinateController;
import lib.util.coordinate.CoordinateUtil.STRAND;
import lib.util.coordinate.advancer.CoordinateAdvancer;
import lib.util.coordinate.OneCoordinate;

public class SharedStorageBuilder implements lib.util.Builder<SharedStorage> {
	
	private final int activeWindowSize;
	private final LibraryType libraryType;
	private final String contig;
	private final Map<String, String> contig2refSeq;
	
	private Coordinate active;
	
	public SharedStorageBuilder(
			final int activeWindowSize, 
			final LibraryType libraryType,
			final String contig,
			final Map<String, String> contig2refSeq) {
		
		this.activeWindowSize 	= activeWindowSize;
		this.libraryType 		= libraryType;
		this.contig				= contig;
		this.contig2refSeq		= contig2refSeq;
	}
	
	public SharedStorageBuilder withActive(final Coordinate active) {
		this.active = active;
		return this;
	}
	
	@Override
	public SharedStorage build() {
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

		if (active != null) {
			coordinateController.helperSetActive(active);
		}
		
		final ReferenceProvider referenceProvider = 
				new SimpleReferenceProvider(coordinateController, contig2refSeq);

		return new ComplexSharedStorage(referenceProvider);
	}
		
	
	
}
