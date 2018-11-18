package jacusa.io.format.rtarrest;

import lib.data.DataTypeContainer;
import lib.data.count.basecall.BaseCallCount;
import lib.data.result.Result;
import lib.io.format.bed.DataAdder;
import lib.util.Util;

public class RTarrestDataAdder implements DataAdder {

	private final BaseCallCount.AbstractParser bccParser;
	
	public RTarrestDataAdder(final BaseCallCount.AbstractParser bccParser) {
		this.bccParser = bccParser; 
	}
	
	@Override
	public void addHeader(StringBuilder sb, int conditionIndex, int replicateIndex) {
		sb.append(Util.FIELD_SEP);
		sb.append(Util.ARREST_BASES);
		sb.append(conditionIndex + 1);
		sb.append(replicateIndex + 1);

		sb.append(Util.FIELD_SEP);
		sb.append(Util.THROUGH_BASES);
		sb.append(conditionIndex + 1);
		sb.append(replicateIndex + 1);
	}
	
	@Override
	public void addData(StringBuilder sb, int valueIndex, int conditionIndex, int replicateIndex, Result result) {
		final DataTypeContainer container = result.getParellelData().getDataContainer(conditionIndex, replicateIndex);
		sb.append(Util.FIELD_SEP);
		sb.append(bccParser.wrap(container.getArrestBaseCallCount()));
		sb.append(Util.FIELD_SEP);
		sb.append(bccParser.wrap(container.getThroughBaseCallCount()));
	}

}