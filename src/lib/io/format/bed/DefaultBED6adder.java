package lib.io.format.bed;

import lib.data.ParallelData;
import lib.data.result.Result;
import lib.util.Util;

public class DefaultBED6adder implements BED6adder {

	private final String fieldName;
	private final String statName;
		
	public DefaultBED6adder(final String fieldName, final String statName) {
		this.fieldName = fieldName;
		this.statName = statName;
	}
	
	@Override
	public void addHeader(StringBuilder sb) {
		sb.append(Util.COMMENT);

		// position (0-based)
		sb.append("contig");
		sb.append(Util.FIELD_SEP);
		sb.append("start");
		sb.append(Util.FIELD_SEP);
		sb.append("end");
		sb.append(Util.FIELD_SEP);

		sb.append("name");
		sb.append(Util.FIELD_SEP);

		// name of statistic column can be customized
		sb.append(statName);
		sb.append(Util.FIELD_SEP);

		sb.append("strand");
	}

	@Override
	public void addData(StringBuilder sb, int valueIndex, Result result) {
		final ParallelData parallelData = result.getParellelData();

		// coordinates
		sb.append(parallelData.getCoordinate().getContig());
		sb.append(Util.FIELD_SEP);
		sb.append(parallelData.getCoordinate().getStart() - 1);
		sb.append(Util.FIELD_SEP);
		sb.append(parallelData.getCoordinate().getEnd());
		
		sb.append(Util.FIELD_SEP);
		sb.append(fieldName);
		
		sb.append(Util.FIELD_SEP);
		sb.append(getStatistic(valueIndex, result));

		sb.append(Util.FIELD_SEP);
		sb.append(parallelData.getCombinedPooledData().getCoordinate().getStrand().character());
	}
	
	private String getStatistic(final int valueIndex, final Result result) {
		final double stat = result.getStat(valueIndex);
		if (Double.isNaN(stat)) {
			return Character.toString(Util.EMPTY_FIELD);
		}
		return Double.toString(stat);
	}
	
}
