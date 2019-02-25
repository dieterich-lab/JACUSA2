package jacusa.filter.factory.exclude;

import lib.util.coordinate.Coordinate;

/**
 * Simple interface that indicates if a coordinate is contained.
 */
public interface ContainedCoordinate {

	boolean isContained(Coordinate coordinate);
	
}