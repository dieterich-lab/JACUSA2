package lib.data.adder;

import lib.data.DataTypeContainer;
import lib.util.coordinate.Coordinate;

public interface DataContainerAdder {

	void populate(DataTypeContainer container, final Coordinate coordinate);
	void clear();
	
}
