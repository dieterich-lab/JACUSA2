package lib.location;

import lib.util.coordinate.Coordinate;

public interface CoordinateAdvancer {

	Coordinate getCurrentCoordinate();

	void advance();
	void adjust(final Coordinate newCoordinate);

}
