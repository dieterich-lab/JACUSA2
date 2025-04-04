package lib.data.storage.container;

import lib.record.Record;
import lib.util.coordinate.CoordinateController;

/**
 * DOCUMENT
 */
public interface SharedStorage {

	int getNext(int winPos);
	ReferenceProvider getReferenceProvider();
	
	void addrecord(Record record);
	void clear();

	CoordinateController getCoordinateController();
	
}