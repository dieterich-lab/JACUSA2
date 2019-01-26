package lib.location;

import lib.util.coordinate.Coordinate;
import lib.util.coordinate.CoordinateUtil.STRAND;

// has JUNIT tests
public class StrandedCoordinateAdvancer implements CoordinateAdvancer {

	private Coordinate coordinate;
	
	public StrandedCoordinateAdvancer(final Coordinate coordinate) {
		this.coordinate = coordinate;
	}

	@Override
	public void advance() {
		if (coordinate.getStrand() == STRAND.FORWARD) {
			coordinate.setStrand(STRAND.REVERSE);
		} else if (coordinate.getStrand() == STRAND.REVERSE){
			coordinate.setStrand(STRAND.FORWARD);
			final int onePosition = coordinate.get1Start() + 1;
			coordinate.set1Position(onePosition);
		} else {
			throw new IllegalStateException("Stranded coordinate cannot be: " + coordinate.getStrand());
		}
	}

	@Override
	public void adjustPosition(final Coordinate coordinate) {
		this.coordinate.setPosition(coordinate);
		this.coordinate.setStrand(coordinate.getStrand());
	}

	@Override
	public Coordinate getCurrentCoordinate() {
		return coordinate;
	}

}
