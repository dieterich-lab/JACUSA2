package jacusa.filter.storage;

import lib.data.builder.SAMRecordWrapper;
import lib.data.builder.SAMRecordWrapper.Position;

public interface ProcessDeletionOperator {

	public abstract void processDeletionOperator(final Position position, final SAMRecordWrapper recordWrapper);

	public abstract char getC();

}