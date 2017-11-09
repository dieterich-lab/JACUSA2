package lib.location;

import lib.util.Coordinate;

public interface CoordinateAdvancer {

	Coordinate getCurrentCoordinate();
	Coordinate nextCoordinate();

	void advance();
	void adjust(final Coordinate newCoordinate);

}
