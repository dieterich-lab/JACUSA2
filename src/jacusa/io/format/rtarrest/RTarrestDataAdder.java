package jacusa.io.format.rtarrest;

import lib.data.DataContainer;
import lib.data.count.basecall.BaseCallCount;
import lib.data.result.Result;
import lib.io.InputOutput;
import lib.io.format.bed.DataAdder;

public class RTarrestDataAdder implements DataAdder {

	private final BaseCallCount.AbstractParser bccParser;
	
	public RTarrestDataAdder(final BaseCallCount.AbstractParser bccParser) {
		this.bccParser = bccParser; 
	}
	
	@Override
	public void addHeader(StringBuilder sb, int condI, int replicateI) {
		sb.append(InputOutput.FIELD_SEP);
		sb.append(InputOutput.ARREST_BASES);
		sb.append(condI + 1);
		sb.append(replicateI + 1);

		sb.append(InputOutput.FIELD_SEP);
		sb.append(InputOutput.THROUGH_BASES);
		sb.append(condI + 1);
		sb.append(replicateI + 1);
	}
	
	@Override
	public void addData(StringBuilder sb, int valueIndex, int condI, int replicateI, Result result) {
		final DataContainer container = result.getParellelData().getDataContainer(condI, replicateI);
		/* TODO replace before
		sb.append(InputOutput.FIELD_SEP);
		sb.append(bccParser.wrap(container.getArrestBaseCallCount()));
		sb.append(InputOutput.FIELD_SEP);
		sb.append(bccParser.wrap(container.getThroughBaseCallCount()));
		*/
	}

}
