package lib.data.cache.container;

import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.util.Base;
import lib.util.coordinate.CoordinateController;

public interface SharedCache {

	int getNext(int windowPosition);
	ReferenceProvider getReferenceProvider();
	Base getReferenceBase(int windowPosition);
	
	void addRecordWrapper(SAMRecordWrapper recordWrapper);
	void clear();

	CoordinateController getCoordinateController();
	
}