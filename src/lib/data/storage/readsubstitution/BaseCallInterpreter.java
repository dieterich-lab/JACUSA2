package lib.data.storage.readsubstitution;

import lib.record.Record;
import lib.util.Base;
import lib.util.LibraryType;
import lib.util.position.Position;

/**
 * TODO
 */
public interface BaseCallInterpreter {

	Base getReadBase(Record record, int readPos);
	Base getRefBase(Record record, Position pos);

	public static BaseCallInterpreter build(final LibraryType libraryType) { 
		switch (libraryType) {

		case UNSTRANDED:
			return new UnstrandedBaseCallInterpreter();

		case RF_FIRSTSTRAND:
		case FR_SECONDSTRAND:
			return new StrandedBaseCallInterpreter();
			
		default:
			throw new IllegalArgumentException("Cannot determine read arrest and read through from library type: " + libraryType.toString());

		}
	}
	
}
