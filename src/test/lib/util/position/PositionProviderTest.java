package test.lib.util.position;

import java.util.ArrayList;
import java.util.List;

import lib.util.position.UnmodifiablePosition;
import lib.record.Record;
import lib.util.position.Position;

public interface PositionProviderTest {

	default List<Position> parseExpected(final String[] str, final Record record) {
		List<Position> positions = new ArrayList<Position>(str.length);
		for (final String tmpStr : str) {
			final String[] cols = tmpStr.split(",");
			final int refPos = Integer.parseInt(cols[0]);
			final int readPos = Integer.parseInt(cols[1]);
			final int winPos = Integer.parseInt(cols[2]);
			positions.add(new UnmodifiablePosition(refPos, readPos, winPos, record));
		}
		return positions;
	}
	
}
