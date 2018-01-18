package lib.data.cache.container;

import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.util.coordinate.Coordinate;

public interface ReferenceProvider {

	void addRecordWrapper(final SAMRecordWrapper recordWrapper);
	byte getReference(Coordinate coordinate);
	byte getReference(int windowPosition);

	void update();
	
}
