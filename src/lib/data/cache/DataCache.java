package lib.data.cache;

import lib.data.AbstractData;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.tmp.CoordinateController;
import lib.util.coordinate.Coordinate;

public interface DataCache<X extends AbstractData> {

	void addData(X data, final Coordinate coordinate);

	CoordinateController getCoordinateController();
	
	void addRecordWrapper(final SAMRecordWrapper recordWrapper);
	void clear();
	
}
