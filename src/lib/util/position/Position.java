package lib.util.position;

import htsjdk.samtools.SAMRecord;
import lib.record.ProcessedRecord;
import lib.util.Base;
import lib.util.Copyable;

/**
 * TODO
 */
public interface Position extends Copyable<Position> {

	// 1-based
	int getReferencePosition();
	
	boolean isValidRefPos();
	
	// 0-based
	int getReadPosition();
	
	default Base getReadBaseCall() {
		return Base.valueOf(getSAMRecord().getReadBases()[getReadPosition()]);
	}
	
	default byte getReadBase() {
		return getSAMRecord().getReadBases()[getReadPosition()];
	}
	
	default byte getReadBaseCallQuality() {
		return getSAMRecord().getBaseQualities()[getReadPosition()];
	}
	
	ProcessedRecord getProcessedRecord();
	
	// convenience
	default SAMRecord getSAMRecord() {
		return getProcessedRecord().getSAMRecord();
	}
	
	// can be invalid -> -1
	// 0-based
	int getWindowPosition();
	
	default boolean isWithinWindow() {
		return getWindowPosition() >= 0;
	}
	
}