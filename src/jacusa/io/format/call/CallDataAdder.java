package jacusa.io.format.call;

import lib.data.DataContainer;
import lib.data.count.basecall.BaseCallCount;
import lib.data.result.Result;
import lib.io.InputOutput;
import lib.io.format.bed.DataAdder;

public class CallDataAdder implements DataAdder {

	private final BaseCallCount.AbstractParser bccParser;
	
	public CallDataAdder(final BaseCallCount.AbstractParser bccParser) {
		this.bccParser = bccParser; 
	}
	
	@Override
	public void addHeader(StringBuilder sb, int condI, int replicateI) {
		sb.append(InputOutput.FIELD_SEP);
		sb.append(InputOutput.BASE_FIELD);
		sb.append(condI + 1);
		sb.append(replicateI + 1);
	}
	
	@Override
	public void addData(StringBuilder sb, int valueIndex, int condI, int replicateI, Result result) {
		final DataContainer container = result.getParellelData().getDataContainer(condI, replicateI);
		sb.append(InputOutput.FIELD_SEP);
		final BaseCallCount bcc = container.getPileupCount().getBCC();
		sb.append(bccParser.wrap(bcc));
	}

}
