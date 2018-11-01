package jacusa.filter.factory.exclude;

import lib.util.coordinate.Coordinate;

public interface ContainedCoordinate {

	boolean isContained(Coordinate coordinate);
	
}