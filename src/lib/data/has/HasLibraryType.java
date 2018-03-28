package lib.data.has;

public interface HasLibraryType {

	LIBRARY_TYPE getLibraryType();

	enum LIBRARY_TYPE {
		FR_FIRSTSTRAND, 
		FR_SECONDSTRAND,
		UNSTRANDED,
		MIXED
	}

}
