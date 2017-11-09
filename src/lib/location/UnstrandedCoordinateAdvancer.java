package lib.location;

import lib.util.Coordinate;
import lib.util.Coordinate.STRAND;

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
	public void adjustPosition(final int position, STRAND strand) {
		coordinate.setPosition(position);
	}
	
}
