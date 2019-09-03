package jacusa.io.format.lrtarrest;

import java.util.List;

import lib.data.DataContainer;
import lib.data.result.Result;
import lib.io.InputOutput;
import lib.io.format.bed.BED6adder;

public class LRTarrestBED6adder implements BED6adder {

	private final BED6adder bed6Adder;
		
	public LRTarrestBED6adder(final BED6adder bed6Adder) {
		this.bed6Adder = bed6Adder;
	}
	
	@Override
	public void addHeader(StringBuilder sb) {
		bed6Adder.addHeader(sb);
		sb.append(InputOutput.FIELD_SEP);
		sb.append("arrest_pos"); // expected to be given 1-indexed
	}
	
	@Override
	public void addData(StringBuilder sb, int valueIndex, Result result) {
		bed6Adder.addData(sb, valueIndex, result);
		sb.append(InputOutput.FIELD_SEP);
		final DataContainer combinedPooledContainer = result.getParellelData().getCombPooledData();
		final List<Integer> arrestPositions = combinedPooledContainer.getArrestPos2BCC().getPositions();
		sb.append(arrestPositions.get(valueIndex));
	}
	
}
