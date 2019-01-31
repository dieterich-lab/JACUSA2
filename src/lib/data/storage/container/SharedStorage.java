package lib.data.storage.container;

import lib.util.coordinate.CoordinateController;
import lib.recordextended.SAMRecordExtended;

public interface SharedStorage {

	int getNext(int winPos);
	ReferenceProvider getReferenceProvider();
	
	void addRecordExtended(SAMRecordExtended recordExtended);
	void clear();

	CoordinateController getCoordinateController();
	
}