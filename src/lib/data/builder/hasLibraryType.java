package lib.data.builder;

public interface hasLibraryType {

	public LIBRARY_TYPE getLibraryType();
	
	public enum LIBRARY_TYPE {
		FR_FIRSTSTRAND, 
		FR_SECONDSTRAND,
		UNSTRANDED
	}

}
