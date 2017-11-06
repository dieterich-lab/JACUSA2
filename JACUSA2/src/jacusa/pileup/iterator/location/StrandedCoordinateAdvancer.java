package jacusa.pileup.iterator.location;

import jacusa.util.Coordinate;
import jacusa.util.Coordinate.STRAND;

public class StrandedCoordinateAdvancer implements CoordinateAdvancer {

	protected Coordinate coordinate;
	
	public StrandedCoordinateAdvancer(final Coordinate coordinate) {
		this.coordinate = coordinate;
	}

	@Override
	public void advance() {
		if (coordinate.getStrand() == STRAND.FORWARD) {
			coordinate.setStrand(STRAND.REVERSE);
		} else {
			coordinate.setStrand(STRAND.FORWARD);
			final int currentPosition = coordinate.getStart() + 1;
			coordinate.setPosition(currentPosition);
		}
	}

	@Override
	public void adjustPosition(final int position, final STRAND strand) {
		coordinate.setPosition(position);
		coordinate.setStrand(strand);
	}

	public Coordinate getCurrentCoordinate() {
		return coordinate;
	}

	@Override
	public Coordinate nextCoordinate() {
		StrandedCoordinateAdvancer tmp = new StrandedCoordinateAdvancer(coordinate);
		tmp.advance();
		return tmp.getCurrentCoordinate();
	}

}
