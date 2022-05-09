package lib.data.storage.container;

import lib.record.ProcessedRecord;
import lib.util.Base;
import lib.util.coordinate.Coordinate;
import lib.util.coordinate.CoordinateController;

/**
 * TODO
 */
public interface ReferenceProvider {

	void addrecord(final ProcessedRecord record);
	Base getReferenceBase(Coordinate coordinate);
	Base getReferenceBase(int winPos);
	void update();

	CoordinateController getCoordinateController();
	
	void close();
	
}
