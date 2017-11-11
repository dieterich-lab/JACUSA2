package lib.data.cache;

import lib.data.AbstractData;
import lib.data.builder.SAMRecordWrapper;
import lib.method.AbstractMethodFactory;
import lib.util.Coordinate;

public interface Cache<T extends AbstractData> {

	void addRecordWrapper(final SAMRecordWrapper recordWrapper);
	void addRecordWrapperPosition(final int readPosition, final SAMRecordWrapper recordWrapper);
	void addRecordWrapperRegion(final int readPosition, final int length, final SAMRecordWrapper recordWrapper);
	
	void clear();

	AbstractMethodFactory<T> getMethodFactory();
	T getData(final Coordinate coordinate);

}
