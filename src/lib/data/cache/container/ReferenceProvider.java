package lib.data.cache.container;

import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.util.Base;
import lib.util.coordinate.Coordinate;
import lib.util.coordinate.CoordinateController;

public interface ReferenceProvider {

	void addRecordWrapper(final SAMRecordWrapper recordWrapper);
	Base getReferenceBase(Coordinate coordinate);
	Base getReferenceBase(int windowPosition);
	void update();

	CoordinateController getCoordinateController();
	
	void close();
	
}
