package lib.util.coordinate;

public interface CoordinateTranslator {

	// reference based coordinates of window [start, end]
	int getRefPosStart();
	int getRefPosEnd();

	int getLength();

	int ref2winPos(int refPos);
	
	default int coord2winPos(Coordinate coordinate) {
		return ref2winPos(coordinate.get1Position());
	}
	
	// make sure winPos is within window!
	default int win2refPos(int winPos) {
		return getRefPosStart() + winPos;
	}
	
}