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

	/**
	 * # number of bases
	 * > 	0	downstream of windowEnd
	 * == 	0	within window
	 * < 	0	upstream of windowStart
	 * @param refPos
	 * @return
	 */
	default int getWindowOffset(int refPos) {
		int offset = refPos - getRefPosStart();
		if (offset < 0) {
			return offset;
		}
		offset = refPos - getRefPosEnd();
		if (offset > 0) {
			return offset;
		}
		return 0;
	}
	
}