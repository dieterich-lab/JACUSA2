package jacusa.io.format.lrtarrest;

import java.util.List;

import lib.data.DataContainer;
import lib.data.DataType;
import lib.data.result.Result;
import lib.data.storage.lrtarrest.ArrestPosition2BaseCallCount;
import lib.io.InputOutput;
import lib.io.format.bed.BED6adder;

public class LRTarrestBED6adder implements BED6adder {

	private final DataType<ArrestPosition2BaseCallCount> ap2bccDt;
	private final BED6adder bed6Adder;
	
	public LRTarrestBED6adder(final DataType<ArrestPosition2BaseCallCount> ap2bccDt, final BED6adder bed6Adder) {
		this.ap2bccDt = ap2bccDt;
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
		final List<Integer> arrestPositions = combinedPooledContainer.get(ap2bccDt).getPositions();
		sb.append(arrestPositions.get(valueIndex));
	}
	
}
