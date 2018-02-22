package lib.data.has;

public interface hasLibraryType {

	LIBRARY_TYPE getLibraryType();

	enum LIBRARY_TYPE {
		FR_FIRSTSTRAND, 
		FR_SECONDSTRAND,
		UNSTRANDED,
		MIXED
	}

}
