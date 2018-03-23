package lib.data.cache.region;

import lib.data.AbstractData;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.util.coordinate.CoordinateController;
import lib.util.coordinate.Coordinate;

public interface RegionDataCache<X extends AbstractData> {

	void addData(X data, final Coordinate coordinate);

	CoordinateController getCoordinateController();
	
	void addRecordWrapperRegion(final int referencePosition, int readPosition, int length, SAMRecordWrapper recordWrapper);
	void clear();
	
}
