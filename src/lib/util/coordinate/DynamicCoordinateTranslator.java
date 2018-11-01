package lib.util.coordinate;

public class DynamicCoordinateTranslator implements CoordinateTranslator {

	private final CoordinateController coordinateController;
	
	public DynamicCoordinateTranslator(final CoordinateController coordinateController) {
		this.coordinateController = coordinateController;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || ! (obj instanceof CoordinateTranslator)) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		
		final DynamicCoordinateTranslator coordinateTranslator = (DynamicCoordinateTranslator)obj;
		return coordinateController.equals(coordinateTranslator.coordinateController);
	}
	
	@Override
	public int hashCode() {
		return coordinateController.hashCode();
	}
	
	@Override
	public int getRefPosStart() {
		return coordinateController.getActive().getStart();
	}
	
	@Override
	public int getRefPosEnd() {
		return coordinateController.getActive().getEnd();
	}
	
	@Override
	public int getLength() {
		return coordinateController.getActiveWindowSize();
	}
	
	@Override
	public int convert2windowPosition(final int refPos) {
		if (refPos > getRefPosEnd() || refPos < getRefPosStart()){
			return -1;
		}
		return refPos - getRefPosStart();
	}
	
	@Override
	public int convert2windowPosition(final Coordinate coordinate) {
		return convert2windowPosition(coordinate.getPosition());
	}
	
	@Override
	public int convert2referencePosition(final int winPos) {
		return getRefPosStart() + winPos;
	}
	
}
