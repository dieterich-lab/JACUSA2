package lib.data.storage.container;

import lib.util.Base;
import lib.util.coordinate.Coordinate;
import lib.util.coordinate.CoordinateController;
import lib.recordextended.SAMRecordExtended;

public interface ReferenceProvider {

	void addRecordExtended(final SAMRecordExtended recordExtended);
	Base getReferenceBase(Coordinate coordinate);
	Base getReferenceBase(int winPos);
	void update();

	CoordinateController getCoordinateController();
	
	void close();
	
}
