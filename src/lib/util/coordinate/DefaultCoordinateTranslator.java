package lib.util.coordinate;

public class DefaultCoordinateTranslator implements CoordinateTranslator {

	private int refPosStart;
	private int refPosEnd;
	private int length;
	
	public DefaultCoordinateTranslator(final Coordinate coordinate) {
		this(coordinate.getStart(), coordinate.getLength());
	}
	
	public DefaultCoordinateTranslator(final int refPosStart, final int length) {
		this.refPosStart = refPosStart;
		refPosEnd = refPosStart + length - 1;
		this.length = length;
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
				refPosStart == coordinateTranslator.refPosStart &&
				length == coordinateTranslator.length;
	}
	
	@Override
	public int hashCode() {
		int hash = 1;
		hash = 31 * hash + refPosStart;
		hash = 31 * hash + refPosEnd;
		hash = 31 * hash + length;
		return hash;
	}
	
	@Override
	public int getRefPosStart() {
		return refPosStart;
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
	public int convert2windowPosition(final int refPos) {
		if (refPos > refPosEnd || refPos < refPosStart){
			return -1;
		}
		return refPos - refPosStart;
	}
	
	@Override
	public int convert2windowPosition(final Coordinate coordinate) {
		return convert2windowPosition(coordinate.getPosition());
	}
	
	@Override
	public int convert2referencePosition(final int winPos) {
		return refPosStart + winPos;
	}
	
}
