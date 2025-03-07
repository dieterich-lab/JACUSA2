package lib.util.position;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import lib.util.coordinate.CoordinateTranslator;

/**
 * TODO add documentation
 */
public interface PositionProvider extends Iterator<Position> {

	default List<Position> flat() {
		final List<Position> positions = new ArrayList<Position>();
		while (hasNext()) {
			positions.add(next());
		}
		return positions;
	}
	
	static int adjustWindowPos(
			final AbstractPosition pos, int length, 
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
		} else {
			pos.setWindowPosition(winPos);
		}
		
		return length;
	}
	
}
