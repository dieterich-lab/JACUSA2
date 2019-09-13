package lib.data.storage.container;

import lib.record.Record;
import lib.util.Base;
import lib.util.coordinate.Coordinate;
import lib.util.coordinate.CoordinateController;

/**
 * TODO
 */
public interface ReferenceProvider {

	void addrecord(final Record record);
	Base getReferenceBase(Coordinate coordinate);
	Base getReferenceBase(int winPos);
	void update();

	CoordinateController getCoordinateController();
	
	void close();
	
}
