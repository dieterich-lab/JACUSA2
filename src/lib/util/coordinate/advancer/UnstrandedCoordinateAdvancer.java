package lib.util.coordinate.advancer;

import lib.util.coordinate.Coordinate;

public class UnstrandedCoordinateAdvancer implements CoordinateAdvancer {

	private Coordinate coordinate;

	public UnstrandedCoordinateAdvancer(final Coordinate coordinate) {
		this.coordinate = coordinate;
	}
	
	@Override
	public Coordinate getCurrentCoordinate() {
		return coordinate;
	}

	@Override
	public void advance() {
		final int onePosition = coordinate.get1Start() + 1;
		coordinate.set1Position(onePosition);
	}

	@Override
	public void adjustPosition(final Coordinate coordinate) {
		this.coordinate.setPosition(coordinate);
		// TODO test
		// this.coordinate.setStrand(coordinate.getStrand());
	}
	
}
