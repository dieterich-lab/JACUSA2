package jacusa.io.format.lrtarrest;

import lib.data.DataContainer;
import lib.data.DataType;
import lib.data.count.basecall.BaseCallCount;
import lib.data.result.Result;
import lib.data.storage.lrtarrest.ArrestPosition2BaseCallCount;
import lib.io.InputOutput;
import lib.io.format.bed.DataAdder;

public class LRTarrestDataAdder implements DataAdder {

	private final DataType<ArrestPosition2BaseCallCount> ap2bccDt;
	private final BaseCallCount.AbstractParser bccParser;

	public LRTarrestDataAdder(final DataType<ArrestPosition2BaseCallCount> ap2bccDt,
			final BaseCallCount.AbstractParser bccParser) {
		this.ap2bccDt = ap2bccDt;
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
		final ArrestPosition2BaseCallCount ap2bcc = container.get(ap2bccDt);

		int onePosition = -1;
		if (valueIndex == Result.TOTAL) {
			onePosition = result.getParellelData().getCoordinate().get1Position();
		} else {
			onePosition = result.getParellelData().getCombPooledData().get(ap2bccDt).getPositions().get(valueIndex);
		}
		sb.append(InputOutput.FIELD_SEP);
		final BaseCallCount arrestBcc = ap2bcc.getArrestBCC(onePosition);
		sb.append(bccParser.wrap(arrestBcc));

		sb.append(InputOutput.FIELD_SEP);
		final BaseCallCount throughBcc = ap2bcc.getThroughBCC(onePosition);
		sb.append(bccParser.wrap(throughBcc));
	}

}
