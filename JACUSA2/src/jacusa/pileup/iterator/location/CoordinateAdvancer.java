package jacusa.pileup.iterator.location;

import jacusa.util.Coordinate;
import jacusa.util.Coordinate.STRAND;

public interface CoordinateAdvancer {

	void advance();
	
	Coordinate getCurrentCoordinate();
	Coordinate nextCoordinate();

	void adjustPosition(final int newPosition, final STRAND newStrand);

}
