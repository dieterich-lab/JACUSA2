package lib.util.coordinate;

public interface CoordinateTranslator {

	int getRefPosStart();
	int getRefPosEnd();

	int getLength();

	int reference2windowPosition(int refPos);
	
	default int coordinate2windowPosition(Coordinate coordinate) {
		return reference2windowPosition(coordinate.get1Position());
	}
	
	// make sure winPos is within window!
	default int window2referencePosition(int winPos) {
		return getRefPosStart() + winPos;
	}
	
}