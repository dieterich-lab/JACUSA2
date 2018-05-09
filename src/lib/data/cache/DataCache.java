package lib.data.cache;

import lib.data.AbstractData;
import lib.util.coordinate.CoordinateController;
import lib.util.coordinate.Coordinate;

public interface DataCache<X extends AbstractData> {

	void addData(X data, final Coordinate coordinate);

	CoordinateController getCoordinateController();

	void clear();
	
}
