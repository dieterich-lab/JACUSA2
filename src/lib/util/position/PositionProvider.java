package lib.util.position;

import java.util.Iterator;

import lib.util.coordinate.CoordinateTranslator;

public interface PositionProvider extends Iterator<Position> {

	static int adjustForWindow(
			final MatchPosition pos, int length, 
			final CoordinateTranslator translator) {
		
		int winPos 	= pos.getReferencePosition() - translator.getRefPosStart();
		if (winPos < 0) {
			length 	= Math.max(0, length + winPos);
			pos.offset(-winPos);
			winPos 	+= -winPos;
		}

		final int offset = translator.getLength() - (winPos + length);
		if (offset < 0) {
			length = Math.max(0, length + offset);
		}

		if (winPos < 0 || winPos >= translator.getLength()) {
			pos.resetWindowPosition();
		}
		
		return length;
	}
	
}
