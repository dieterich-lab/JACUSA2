package lib.data.cache;

import lib.data.AbstractData;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.util.coordinate.Coordinate;

public interface Cache<X extends AbstractData> {

	void addRecordWrapperPosition(final int readPosition, final SAMRecordWrapper recordWrapper);
	void addRecordWrapperRegion(final int readPosition, final int length, final SAMRecordWrapper recordWrapper);
	void addRecordWrapper(final SAMRecordWrapper recordWrapper);
	
	void clear();

	void addData(X data, final Coordinate coordinate);
	
}
