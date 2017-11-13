package lib.data.cache;

import lib.data.AbstractData;
import lib.data.builder.SAMRecordWrapper;
import lib.data.generator.DataGenerator;
import lib.util.Coordinate;

public interface Cache<X extends AbstractData> {

	void addRecordWrapperPosition(final int readPosition, final SAMRecordWrapper recordWrapper);
	void addRecordWrapperRegion(final int readPosition, final int length, final SAMRecordWrapper recordWrapper);
	void addRecordWrapper(final SAMRecordWrapper recordWrapper);
	
	
	
	
	void clear();

	DataGenerator<X> getDataGenerator();
	X getData(final Coordinate coordinate);
	void setActiveWindowCoordinate(final Coordinate coordinate);
	Coordinate getActiveWindowCoordinate();
	
	public class WindowPosition {
		
		int i;
		int leftOffset;
		int rightOffset;
		
	}
	
}
