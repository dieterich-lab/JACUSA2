package jacusa.filter.storage;

import lib.data.builder.SAMRecordWrapper;
import lib.data.builder.SAMRecordWrapper.Position;

public interface ProcessInsertionOperator {

	public abstract void processInsertionOperator(final Position position, final SAMRecordWrapper recordWrapper);
	
	public abstract char getC();

}