package lib.location;

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
		final int currentPosition = coordinate.getStart() + 1;
		coordinate.setPosition(currentPosition);
	}
	
	@Override
	public Coordinate nextCoordinate() {
		UnstrandedCoordinateAdvancer tmp = new UnstrandedCoordinateAdvancer(coordinate);
		tmp.advance();
		return tmp.getCurrentCoordinate();
	}

	@Override
	public void adjust(final Coordinate coordinate) {
		this.coordinate.setPosition(coordinate.getPosition());
		this.coordinate.setStrand(coordinate.getStrand());
	}
	
}
