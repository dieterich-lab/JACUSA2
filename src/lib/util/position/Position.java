package lib.util.position;

import htsjdk.samtools.SAMRecord;
import lib.util.Base;
import lib.util.Copyable;
import lib.recordextended.SAMRecordExtended;

public interface Position extends Copyable<Position> {

	// TODO what if INSERTION?
	// 1-based
	int getReferencePosition();
	
	boolean isValidReferencePosition();
	
	// expected to be always valid -> within range of getRecord().getReadBases() 
	// 0-based
	int getReadPosition();
	
	default Base getReadBaseCall() {
		return Base.valueOf(getRecord().getReadBases()[getReadPosition()]);
	}
	
	default byte getReadBaseCallQuality() {
		return getRecord().getBaseQualities()[getReadPosition()];
	}
	
	SAMRecordExtended getRecordExtended();

	// convenience
	default SAMRecord getRecord() {
		return getRecordExtended().getSAMRecord();
	}
	
	// can be invalid -> -1
	// 0-based
	int getWindowPosition();
	
	default boolean isWithinWindow() {
		return getWindowPosition() >= 0;
	}
	
}