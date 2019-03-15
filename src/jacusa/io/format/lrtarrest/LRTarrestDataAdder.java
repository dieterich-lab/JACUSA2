package jacusa.io.format.lrtarrest;

import java.util.ArrayList;
import java.util.List;

import htsjdk.samtools.util.StringUtil;
import lib.data.DataContainer;
import lib.data.count.basecall.BaseCallCount;
import lib.data.result.Result;
import lib.data.storage.lrtarrest.ArrestPosition2baseCallCount;
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
	public void addData(StringBuilder sb, int valueIndex, int conditionIndex, int replicateIndex, Result result) {
		final DataContainer container 				= result.getParellelData().getDataContainer(conditionIndex, replicateIndex);
		final ArrestPosition2baseCallCount ap2bcc 	= container.getArrestPos2BaseCallCount();
		
		final List<String> tmp = new ArrayList<>(2);
		
		int onePosition = -1;
		if (valueIndex == Result.TOTAL) {
			onePosition = result.getParellelData().getCoordinate().get1Position();
		} else {
			onePosition = result.getParellelData().getCombinedPooledData()
					.getArrestPos2BaseCallCount().getPositions().get(valueIndex);
		}
		tmp.add(bccParser.wrap(ap2bcc.getArrestBaseCallCount(onePosition)));
		tmp.add(bccParser.wrap(ap2bcc.getThroughBaseCallCount(onePosition)));
		
		sb.append(InputOutput.FIELD_SEP);
		sb.append(
				StringUtil.join(
						Character.toString(InputOutput.FIELD_SEP),
						tmp) );
	}
	
}
