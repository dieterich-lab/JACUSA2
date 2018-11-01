package jacusa.io.format.lrtarrest;

import java.util.ArrayList;
import java.util.List;

import lib.data.DataTypeContainer;
import lib.data.has.HasCoordinate;
import lib.data.result.Result;
import lib.io.format.bed.BED6adder;
import lib.util.Util;
import lib.util.coordinate.Coordinate;

public class LRTarrestBED6adder implements BED6adder {

	private final BED6adder bed6Adder;
		
	public LRTarrestBED6adder(final BED6adder bed6Adder) {
		this.bed6Adder = bed6Adder;
	}
	
	@Override
	public void addHeader(StringBuilder sb) {
		bed6Adder.addHeader(sb);
		sb.append(Util.FIELD_SEP);
		sb.append("arrest_pos"); // 0-index
	}

	@Override
	public void addData(StringBuilder sb, int valueIndex, Result result) {
		bed6Adder.addData(sb, valueIndex, result);
		sb.append(Util.FIELD_SEP);
		final DataTypeContainer combinedPooledContainer = result.getParellelData().getCombinedPooledData();
		final List<Integer> arrestPositions = new ArrayList<>(
				combinedPooledContainer.getArrestPos2BaseCallCount().getArrestPos());
		if (isArrestPosition(arrestPositions.get(valueIndex), combinedPooledContainer)) {
			sb.append(Util.EMPTY_FIELD);
		} else {
			sb.append(arrestPositions.get(valueIndex) - 1);
		}
	}
	
	private boolean isArrestPosition(final int position, final HasCoordinate object) {
		final Coordinate currentCoord = object.getCoordinate();
		return currentCoord.getPosition() == position;
	}
	
}
