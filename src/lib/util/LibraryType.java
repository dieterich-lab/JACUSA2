package lib.util;

public enum LibraryType {
	RF_FIRSTSTRAND, 
	FR_SECONDSTRAND,
	UNSTRANDED,
	MIXED;
	
	public static LibraryType mergeLibraryType(final LibraryType l1, final LibraryType l2) {
		return l1 == l2 ? l1 : LibraryType.MIXED;  
	}
	
	public static boolean isStranded(final LibraryType libraryType) {
		switch (libraryType) {
		case UNSTRANDED:
			return false;
			
		case RF_FIRSTSTRAND:
		case FR_SECONDSTRAND:
			return true;
			
		default:
			throw new IllegalArgumentException("Unsupported library type: " + libraryType.toString());
		}
	}
	
}
