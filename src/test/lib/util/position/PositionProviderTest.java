package test.lib.util.position;

import java.util.ArrayList;
import java.util.List;

import lib.recordextended.SAMRecordExtended;
import lib.util.position.DefaultPosition;
import lib.util.position.Position;

public interface PositionProviderTest {

	default List<Position> parseExpected(final String[] str, final SAMRecordExtended recordExtended) {
		List<Position> positions = new ArrayList<Position>(str.length);
		for (final String tmpStr : str) {
			final String[] cols = tmpStr.split(",");
			final int refPos = Integer.parseInt(cols[0]);
			final int readPos = Integer.parseInt(cols[1]);
			final int winPos = Integer.parseInt(cols[2]);
			positions.add(new DefaultPosition(refPos, readPos, winPos, recordExtended));
		}
		return positions;
	}
	
}
