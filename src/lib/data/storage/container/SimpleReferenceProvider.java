package lib.data.storage.container;

import java.util.Map;

import lib.util.coordinate.CoordinateController;
import lib.util.coordinate.CoordinateUtil.STRAND;
import lib.util.coordinate.OneCoordinate;
import lib.record.ProcessedRecord;
import lib.util.Base;
import lib.util.coordinate.Coordinate;

/**
 * TODO
 */
public class SimpleReferenceProvider implements ReferenceProvider {

	private final CoordinateController coordinateController;
	private final Map<String, String> contig2refSeq;
	
	private Coordinate window;
	
	public SimpleReferenceProvider(final CoordinateController coordinateController,
			final Map<String, String> chr2refSeq) {

		this.coordinateController 	= coordinateController;
		this.contig2refSeq 			= chr2refSeq;
	}

	public void addrecord(final ProcessedRecord record) {}

	@Override
	public void update() {
		if (window == null || window != coordinateController.getActive()) {
			window = coordinateController.getActive();
		}
	}
	
	@Override
	public Base getReferenceBase(final Coordinate coordinate) {
		final String contig 	= coordinate.getContig();
		final int zeroPosition 	= coordinate.get0Position();
		final String refSeq		= contig2refSeq.get(contig);
		
		if (zeroPosition >= refSeq.length()) {
			return Base.N;
		}
		
		final char base = refSeq.charAt(zeroPosition);
		return Base.valueOf(base);
	}
	
	@Override
	public Base getReferenceBase(final int winPos) {
		final String contig = window.getContig();
		final int position 	= coordinateController.getCoordinateTranslator().win2refPos(winPos);
		return getReferenceBase(new OneCoordinate(contig, position, STRAND.UNKNOWN));
	}

	@Override
	public CoordinateController getCoordinateController() {
		return coordinateController;
	}
	
	public void close() {}
	
}
