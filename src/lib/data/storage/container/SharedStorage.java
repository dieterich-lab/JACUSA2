package lib.data.storage.container;

import lib.record.ProcessedRecord;
import lib.util.coordinate.CoordinateController;

/**
 * TODO
 */
public interface SharedStorage {
	
	int getNext(int winPos);
	ReferenceProvider getReferenceProvider();
	
	void addRecord(ProcessedRecord record);
	void clear();
	
	CoordinateController getCoordinateController();
	
}