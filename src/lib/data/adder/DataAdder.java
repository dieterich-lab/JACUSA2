package lib.data.adder;

import lib.data.AbstractData;
import lib.util.coordinate.CoordinateController;
import lib.util.coordinate.Coordinate;

public interface DataAdder<T extends AbstractData> {

	void addData(T data, final Coordinate coordinate);

	CoordinateController getCoordinateController();

	void clear();
	
}
