package lib.location;

import lib.util.Coordinate;
import lib.util.Coordinate.STRAND;

public interface CoordinateAdvancer {

	void advance();
	
	Coordinate getCurrentCoordinate();
	Coordinate nextCoordinate();

	void adjustPosition(final int newPosition, final STRAND newStrand);

}
