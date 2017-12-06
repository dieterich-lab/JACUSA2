package lib.location;

import lib.util.coordinate.Coordinate;
import lib.util.coordinate.CoordinateUtil.STRAND;

public class StrandedCoordinateAdvancer implements CoordinateAdvancer {

	protected Coordinate coordinate;
	
	public StrandedCoordinateAdvancer(final Coordinate coordinate) {
		this.coordinate = coordinate;
	}

	@Override
	public void advance() {
		if (coordinate.getStrand() == STRAND.FORWARD) {
			coordinate.setStrand(STRAND.REVERSE);
		} else if (coordinate.getStrand() == STRAND.REVERSE){
			coordinate.setStrand(STRAND.FORWARD);
			final int currentPosition = coordinate.getStart() + 1;
			coordinate.setPosition(currentPosition);
		} else {
			throw new IllegalStateException("Stranded coordinate cannot be: " + coordinate.getStrand());
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

}
