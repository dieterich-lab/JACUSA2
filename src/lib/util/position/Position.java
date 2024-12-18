package lib.util.position;

import htsjdk.samtools.SAMRecord;
import lib.data.count.basecall.BaseCallCount;
import lib.record.Record;
import lib.util.Base;
import lib.util.Copyable;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO add documentation
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
		if (getSAMRecord().getBaseQualities().length == 0) {
			return (byte)40; // FIXME what is the default when no BASQ available
		}
		return getSAMRecord().getBaseQualities()[getReadPosition()];
	}

	/*default List<String> getModifiedBases(){
		List<String> modBases = new ArrayList<>();
		List<Record.Modification> mods = getRecord().getMMValues().get(getReadPosition());

		for (Record.Modification mod : mods) {
			modBases.add(mod.getMod());
		}

		return modBases;
	}*/

	default List<Record.ModificationDetail> getModifications(){
		return getRecord().getMMValues().get(getReadPosition());
	}
	
	Record getRecord();

	// convenience
	default SAMRecord getSAMRecord() {
		return getRecord().getSAMRecord();
	}
	
	// can be invalid -> -1
	// 0-based
	int getWindowPosition();
	
	default boolean isWithinWindow() {
		return getWindowPosition() >= 0;
	}
	
}