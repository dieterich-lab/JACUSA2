package lib.util.coordinate;

public class DefaultCoordinateTranslator implements CoordinateTranslator {

	private int refPoswinStart;
	private int refPosEnd;
	private int length;
	
	public DefaultCoordinateTranslator(final Coordinate coordinate) {
		this(coordinate.getStart(), coordinate.getLength());
	}
	
	public DefaultCoordinateTranslator(final int refPosWinStart, final int length) {
		this.refPoswinStart = refPosWinStart;
		refPosEnd 			= refPosWinStart + length - 1;
		this.length 		= length;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || ! (obj instanceof CoordinateTranslator)) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		
		final DefaultCoordinateTranslator coordinateTranslator = (DefaultCoordinateTranslator)obj;
		return 
				refPoswinStart == coordinateTranslator.refPoswinStart &&
				length == coordinateTranslator.length;
	}
	
	@Override
	public int hashCode() {
		int hash = 1;
		hash = 31 * hash + refPoswinStart;
		hash = 31 * hash + refPosEnd;
		hash = 31 * hash + length;
		return hash;
	}
	
	@Override
	public int getRefPosStart() {
		return refPoswinStart;
	}
	
	@Override
	public int getRefPosEnd() {
		return refPosEnd;
	}
	
	@Override
	public int getLength() {
		return length;
	}
	
	@Override
	public int reference2windowPosition(final int refPos) {
		if (refPos > refPosEnd || refPos < refPoswinStart){
			return -1;
		}
		return refPos - refPoswinStart;
	}
	
}
