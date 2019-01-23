package lib.data.cache.container;

import java.util.Map;

import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.util.coordinate.CoordinateController;
import lib.util.coordinate.CoordinateUtil.STRAND;
import lib.util.Base;
import lib.util.coordinate.Coordinate;

public class SimpleReferenceProvider implements ReferenceProvider {

	private final CoordinateController coordinateController;
	private final Map<String, String> contig2refSeq;
	
	private Coordinate window;
	
	public SimpleReferenceProvider(final CoordinateController coordinateController,
			final Map<String, String> chr2refSeq) {

		this.coordinateController = coordinateController;
		this.contig2refSeq = chr2refSeq;
	}

	public void addRecordWrapper(final SAMRecordWrapper recordWrapper) {}

	@Override
	public void update() {
		if (window == null || window != coordinateController.getActive()) {
			window = coordinateController.getActive();
		}
	}
	
	@Override
	public Base getReferenceBase(final Coordinate coordinate) {
		final String contig = coordinate.getContig();
		final int position = coordinate.getPosition();
		
		final char base = contig2refSeq.get(contig).charAt(position);
		return Base.valueOf(base);
	}
	
	@Override
	public Base getReferenceBase(final int windowPosition) {
		final String contig = window.getContig();
		final int position = coordinateController.getCoordinateTranslator().convert2referencePosition(windowPosition);
		return getReferenceBase(new Coordinate(contig, position, STRAND.UNKNOWN));
	}

	@Override
	public CoordinateController getCoordinateController() {
		return coordinateController;
	}
	
	public void close() {}
	
}
