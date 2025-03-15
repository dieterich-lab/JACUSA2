package jacusa.io.format;

import lib.data.DataContainer;
import lib.data.count.basecall.BaseCallCount;
import lib.data.result.Result;
import lib.io.InputOutput;
import lib.io.format.bed.DataAdder;

public class DefaultDataAdder implements DataAdder {

	private final BaseCallCount.AbstractParser bccParser;
	
	public DefaultDataAdder(final BaseCallCount.AbstractParser bccParser) {
		this.bccParser = bccParser; 
	}
	
	@Override
	public void addHeader(StringBuilder sb, int conditionIndex, int replicateIndex) {
		sb.append(InputOutput.FIELD_SEP);
		sb.append(InputOutput.BASE_FIELD);
		sb.append(conditionIndex + 1);
		sb.append(replicateIndex + 1);
	}
	
	@Override
	public void addData(StringBuilder sb, int valueIndex, int condintionIndex, int replicateIndex, Result result) {
		final DataContainer container = result.getParellelData().getDataContainer(condintionIndex, replicateIndex);
		sb.append(InputOutput.FIELD_SEP);
		final BaseCallCount bcc = container.getPileupCount().getBCC();
		sb.append(bccParser.wrap(bcc));
	}

}
