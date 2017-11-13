package jacusa.filter.storage;

import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.builder.recordwrapper.SAMRecordWrapper.Position;

public interface ProcessSkippedOperator {

	public abstract void processSkippedOperator(final Position position, final SAMRecordWrapper recordWrapper);
	
	public abstract char getC();

}