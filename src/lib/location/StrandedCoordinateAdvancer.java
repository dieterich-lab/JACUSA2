package lib.location;

import lib.util.Coordinate;
import lib.util.Coordinate.STRAND;

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
	public void adjust(final Coordinate coordinate) {
		this.coordinate.setPosition(coordinate.getPosition());
		this.coordinate.setStrand(coordinate.getStrand());
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
