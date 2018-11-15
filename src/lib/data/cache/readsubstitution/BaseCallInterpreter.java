package lib.data.cache.readsubstitution;

import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.has.LibraryType;
import lib.util.Base;

public interface BaseCallInterpreter {

	Base getReadBase(SAMRecordWrapper recordWrapper, int readPosition);
	Base getRefBase(SAMRecordWrapper recordWrapper, int referencePosition);
	
	static BaseCallInterpreter create(final LibraryType libraryType) { 
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
