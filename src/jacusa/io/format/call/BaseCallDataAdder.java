package jacusa.io.format.call;


import lib.data.DataContainer;
import lib.data.DataType;
import lib.data.count.basecall.BaseCallCount;
import lib.data.result.Result;
import lib.io.InputOutput;
import lib.io.format.bed.DataAdder;

public class BaseCallDataAdder implements DataAdder {

	private final DataType<BaseCallCount> dataType;
	private final BaseCallCount.AbstractParser bccParser;

	public BaseCallDataAdder(final DataType<BaseCallCount> dataType, final BaseCallCount.AbstractParser bccParser) {
		this.dataType = dataType;
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
		final BaseCallCount bcc = container.get(dataType);
		sb.append(bccParser.wrap(bcc));
	}

}
