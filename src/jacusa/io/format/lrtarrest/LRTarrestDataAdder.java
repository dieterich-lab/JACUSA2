package jacusa.io.format.lrtarrest;

import lib.data.DataContainer;
import lib.data.count.basecall.BaseCallCount;
import lib.data.result.Result;
import lib.data.storage.lrtarrest.ArrestPos2BCC;
import lib.io.InputOutput;
import lib.io.format.bed.DataAdder;

public class LRTarrestDataAdder implements DataAdder {

	private final BaseCallCount.AbstractParser bccParser;
	
	public LRTarrestDataAdder(final BaseCallCount.AbstractParser bccParser) {
		this.bccParser = bccParser; 
	}
	
	@Override
	public void addHeader(StringBuilder sb, int conditionIndex, int replicateIndex) {
		sb.append(InputOutput.FIELD_SEP);
		sb.append(InputOutput.ARREST_BASES);
		sb.append(conditionIndex + 1);
		sb.append(replicateIndex + 1);

		sb.append(InputOutput.FIELD_SEP);
		sb.append(InputOutput.THROUGH_BASES);
		sb.append(conditionIndex + 1);
		sb.append(replicateIndex + 1);
	}
	
	@Override
	public void addData(StringBuilder sb, int valueIndex, int condintionIndex, int replicateIndex, Result result) {
		final DataContainer container 	= result.getParellelData().getDataContainer(condintionIndex, replicateIndex);
		final ArrestPos2BCC ap2bcc 		= container.getArrestPos2BCC();
		
		int onePosition = -1;
		if (valueIndex == Result.TOTAL) {
			onePosition = result.getParellelData().getCoordinate().get1Position();
		} else {
			onePosition = result.getParellelData().getCombPooledData()
					.getArrestPos2BCC().getPositions().get(valueIndex);
		}
		sb.append(InputOutput.FIELD_SEP);
		final BaseCallCount arrestBcc = ap2bcc.getArrestBCC(onePosition);
		sb.append(bccParser.wrap(arrestBcc));
		
		sb.append(InputOutput.FIELD_SEP);
		final BaseCallCount throughBcc = ap2bcc.getThroughBCC(onePosition);
		sb.append(bccParser.wrap(throughBcc));
	}
	
}
