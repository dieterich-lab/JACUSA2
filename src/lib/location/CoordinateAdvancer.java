package lib.location;

import lib.util.coordinate.Coordinate;

public interface CoordinateAdvancer {

	Coordinate getCurrentCoordinate();
	Coordinate nextCoordinate();

	void advance();
	void adjust(final Coordinate newCoordinate);

}
